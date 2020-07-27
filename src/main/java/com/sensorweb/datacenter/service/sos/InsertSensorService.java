package com.sensorweb.datacenter.service.sos;

import com.sensorweb.datacenter.dao.*;
import com.sensorweb.datacenter.entity.*;
import com.sensorweb.datacenter.entity.Event;
import com.sensorweb.datacenter.util.DataCenterUtils;
import net.opengis.OgcPropertyList;
import net.opengis.gml.v32.impl.GMLFactory;
import net.opengis.sensorml.v20.*;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.Factory;
import org.apache.commons.lang3.StringUtils;
import org.isotc211.v2005.gco.impl.GCOFactory;
import org.isotc211.v2005.gmd.CIAddress;
import org.isotc211.v2005.gmd.CIResponsibleParty;
import org.isotc211.v2005.gmd.CITelephone;
import org.isotc211.v2005.gmd.impl.GMDFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.vast.data.SWEFactory;
import org.vast.ows.OWSException;
import org.vast.ows.sos.InsertSensorReaderV20;
import org.vast.ows.sos.InsertSensorRequest;
import org.vast.ows.sos.InsertSensorResponse;
import org.vast.ows.sos.InsertSensorWriterV20;
import org.vast.ows.swe.InsertSensorResponseWriterV20;
import org.vast.sensorML.SMLFactory;
import org.vast.sensorML.SMLUtils;
import org.vast.util.TimeExtent;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.stream.*;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


@Service
public class InsertSensorService {

    @Autowired
    private ProcedureMapper procedureMapper;

    @Autowired
    private OfferingMapper offeringMapper;

    @Value("${datacenter.path.upload}")
    private String uploadPath;


    /**
     * 生成InsertSensor响应文档
     * @param procedureId
     * @param offering
     * @return
     */
    public Element getInsertSensorResponse(String procedureId, String offering) throws OWSException {
        InsertSensorResponse response = new InsertSensorResponse();
        response.setAssignedOffering(offering);
        response.setAssignedProcedureId(procedureId);

        InsertSensorResponseWriterV20 writer = new InsertSensorResponseWriterV20();
        DOMHelper domHelper = new DOMHelper();

        return writer.buildXMLResponse(domHelper, response, "2.0");
    }

    /**
     * 解析InsertSensor请求参数，返回InsertSensorRequest对象，该对象包含sensorML的所有信息，后续的数据存储就是操作此对象
     * @param requestContent
     * @return
     * @throws IOException
     * @throws OWSException
     */
    public InsertSensorRequest getInsertSensorRequest(String requestContent) throws IOException, OWSException {
        if (StringUtils.isBlank(requestContent)) {
            return null;
        }

        DOMHelper domHelper = new DOMHelper(new ByteArrayInputStream(requestContent.getBytes()),false);
        InsertSensorReaderV20 insertSensorReader = new InsertSensorReaderV20();
        InsertSensorRequest insertSensorRequest = insertSensorReader.readXMLQuery(domHelper, domHelper.getRootElement());

        return insertSensorRequest;
    }

    /**
     * 解析InsertSensor请求文档，将其中的snesorml内容写入文件保存，返回文件路径
     * @param insertSensorRequest
     * @return
     * @throws OWSException
     */
    public String getSensorML(InsertSensorRequest insertSensorRequest) throws OWSException {
        DOMHelper domHelper = new DOMHelper();
        InsertSensorWriterV20 insertSensorWriter = new InsertSensorWriterV20();
        Element rootElement = insertSensorWriter.buildXMLQuery(domHelper, insertSensorRequest);
        NodeList nodeList = rootElement.getElementsByTagName("swes:procedureDescription");
        Node node = nodeList.item(0).getFirstChild();

        //写入文件
        String fileName = DataCenterUtils.generateUUID() + ".xml";
        try {
            DataCenterUtils.write2File(uploadPath + "/sensorml/" + fileName, DataCenterUtils.element2String((Element) node));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadPath + "/sensorml/" + fileName;
    }

    /**
     * 存储数据
     * @return
     * @throws ParseException
     * @throws XMLStreamException
     */
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String[] insertSensor(InsertSensorRequest insertSensorRequest) throws OWSException {
        String[] results = new String[2];

        //add procedure
        Procedure procedure = getProcedure(insertSensorRequest);
        if (procedure!=null) {
            int flag = procedureMapper.insertData(procedure);
            if (flag>0) {
                results[0] = procedure.getId();
            }
        }


        //add offering
        List<String> offeringIds = new ArrayList<>();
        List<Offering> offerings = getOfferings(insertSensorRequest);
        if (offerings!=null && offerings.size()>0) {
            for (Offering offering : offerings) {
                int count = offeringMapper.insertData(offering);
                if (count>0) {
                    offeringIds.add(offering.getId());
                }
            }
        }
        results[1] = DataCenterUtils.list2String(offeringIds);

        return results;
    }

    public Keyword getKeyword(InsertSensorRequest insertSensorRequest) {
        Keyword keywords = new Keyword();
        String identifier = insertSensorRequest.getProcedureDescription().getUniqueIdentifier();
        keywords.setIdentifier(identifier + ":keywords");

        OgcPropertyList<KeywordList> keywordLists = insertSensorRequest.getProcedureDescription().getKeywordsList();
        if (keywordLists!=null && keywordLists.size()>0) {
            List<String> keywordTemp = keywordLists.get(0).getKeywordList();
            if (keywordTemp!=null && keywordTemp.size()>0) {
                keywords.setValues(DataCenterUtils.list2String(keywordTemp));
            }
        }
        return keywords;
    }

    /**
     * 解析InsertSensorRequest请求，获取传感器基本信息，并以Procedure对象返回
     * @param insertSensorRequest
     * @return
     * @throws OWSException
     */
    public Procedure getProcedure(InsertSensorRequest insertSensorRequest) throws OWSException {
        Procedure procedure = new Procedure();

        if (insertSensorRequest!=null) {
            procedure.setId(insertSensorRequest.getProcedureDescription().getUniqueIdentifier());
            procedure.setDescription(insertSensorRequest.getProcedureDescription().getDescription());
            procedure.setDescriptionFile(getSensorML(insertSensorRequest));
            procedure.setDescriptionFormat(insertSensorRequest.getProcedureDescriptionFormat());
            procedure.setName(insertSensorRequest.getProcedureDescription().getName());
        }
        return procedure;
    }

    /**
     * 解析InsertSensorRequest请求，获取Offering信息，offering与procedure存在1:n的关系
     * @param request
     * @return
     */
    public List<Offering> getOfferings(InsertSensorRequest request) {
        List<Offering> result = new ArrayList<>();
        if (request!=null) {
            AbstractProcess abstractProcess = request.getProcedureDescription();
            if (abstractProcess!=null) {
                CapabilityList offeringCap = abstractProcess.getCapabilities("offerings");
                if (offeringCap!=null && offeringCap.getCapabilityList()!=null && offeringCap.getCapabilityList().size()>0) {
                    OgcPropertyList<DataComponent> datas = offeringCap.getCapabilityList();
                    for (int i=0; i<datas.size();i++) {
                        Offering temp = new Offering();
                        temp.setProcedureId(request.getProcedureDescription().getUniqueIdentifier());
                        temp.setObservableProperty(DataCenterUtils.list2String(request.getObservableProperties()));
                        temp.setId(datas.get(i).getData().getStringValue());
                        temp.setName(datas.get(i).getLabel());

                        result.add(temp);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 解析PhysicalSystem对象，并加以处理，最后返回自定义Identification对象集合
     * @param
     * @return
     */
    public List<Identification> getIdentification(InsertSensorRequest insertSensorRequest) {
        List<Identification> result = new ArrayList<>();

        OgcPropertyList<IdentifierList> identifierLists = insertSensorRequest.getProcedureDescription().getIdentificationList();
        if (identifierLists!=null && identifierLists.size()>0) {
            List<Term> identifiers = identifierLists.get(0).getIdentifierList();
            if (identifiers!=null && identifiers.size()>0) {
                for (int i = 0; i<identifiers.size(); i++) {
                    Identification identification = new Identification();
                    identification.setDefinition(identifiers.get(i).getDefinition());
                    identification.setIdentifier(insertSensorRequest.getProcedureDescription().getUniqueIdentifier() + ":Identification_No" + (i+1));
                    identification.setLabel(identifiers.get(i).getLabel());
                    identification.setValue(identifiers.get(i).getValue());

                    result.add(identification);
                }
            }
        }

        return result;
    }

    /**
     * 解析PhysicalSystem对象，并加以处理，最后返回自定义Classification对象集合
     * @param
     * @return
     */
    public List<Classification> getClassification(InsertSensorRequest insertSensorRequest) {
        List<Classification> result = new ArrayList<>();

        OgcPropertyList<ClassifierList> classifierLists = insertSensorRequest.getProcedureDescription().getClassificationList();
        if (classifierLists!=null && classifierLists.size()>0) {
            List<Term> terms = classifierLists.get(0).getClassifierList();
            if (terms!=null && terms.size()>0) {
                for (int i=0; i<terms.size(); i++) {
                    Classification classification = new Classification();
                    classification.setIdentifier(insertSensorRequest.getProcedureDescription().getUniqueIdentifier() + ":Classification_No"+(i+1));
                    classification.setDefinition(terms.get(i).getDefinition());
                    classification.setLabel(terms.get(i).getLabel());
                    classification.setValue(terms.get(i).getValue());

                    result.add(classification);
                }
            }
        }
        return result;
    }

    /**
     * 解析PhysicalSystem对象，并加以处理，最后返回自定义ValidTime对象集合
     * @param
     * @return
     * @throws ParseException
     */
    public ValidTime getValidTime(InsertSensorRequest insertSensorRequest) throws ParseException {
        ValidTime result = new ValidTime();

        TimeExtent timeExtent = insertSensorRequest.getProcedureDescription().getValidTime();
        if (timeExtent!=null) {
            result.setIdentifier(insertSensorRequest.getProcedureDescription().getUniqueIdentifier()+":ValidTime");
            if (timeExtent.begin()!=null) {
                result.setBeginPosition(DataCenterUtils.instant2LocalDateTime(timeExtent.begin()));
            }
            if (!timeExtent.end().toString().equals("+1000000000-12-31T23:59:59.999999999Z")) {
                result.setEndPosition(DataCenterUtils.instant2LocalDateTime(timeExtent.end()));
            }
        }

        return result;
    }

    /**
     * 解析PhysicalSystem对象，并加以处理，最后返回自定义Capability对象集合
     * @param
     * @return
     */
    public List<Capability> getCapability(InsertSensorRequest insertSensorRequest) {
        List<Capability> result = new ArrayList<>();

        OgcPropertyList<CapabilityList> capabilityLists = insertSensorRequest.getProcedureDescription().getCapabilitiesList();
        CapabilityList ss = capabilityLists.get("offerings");
        if (capabilityLists!=null && capabilityLists.size()>0) {
            OgcPropertyList<DataComponent> capabilityList = capabilityLists.get(0).getCapabilityList();
            if (capabilityList!=null && capabilityList.size()>0) {
                for (int i=0; i<capabilityList.size(); i++) {
                    Capability capability = new Capability();
                    capability.setIdentifier(insertSensorRequest.getProcedureDescription().getUniqueIdentifier()+":Capability_No"+(i+1));
                    capability.setDefinition(capabilityList.get(i).getDefinition());
                    capability.setLabel(capabilityList.get(i).getLabel());
                    capability.setName(capabilityList.get(i).getName());
                    capability.setValue(capabilityList.get(i).getData().getStringValue());

                    result.add(capability);
                }
            }
        }

        return result;
    }

    /**
     * 解析PhysicalSystem对象，并加以处理，最后返回自定义Characteristic对象集合
     * @param
     * @return
     */
    public List<Characteristic> getCharacteristic(InsertSensorRequest insertSensorRequest) {
        List<Characteristic> result = new ArrayList<>();

        OgcPropertyList<CharacteristicList> characteristicLists = insertSensorRequest.getProcedureDescription().getCharacteristicsList();
        if (characteristicLists!=null && characteristicLists.size()>0) {
            OgcPropertyList<DataComponent> characteristicList = characteristicLists.get(0).getCharacteristicList();
            if (characteristicList!=null && characteristicList.size()>0) {
                for (int i = 0;i<characteristicList.size(); i++) {
                    Characteristic characteristic = new Characteristic();
                    characteristic.setIdentifier(insertSensorRequest.getProcedureDescription().getUniqueIdentifier()+":Characteristic_No"+(i+1));
                    characteristic.setLabel(characteristicList.get(i).getLabel());
                    characteristic.setName(characteristicList.get(i).getName());
                    characteristic.setValue(characteristicList.get(i).getData().getStringValue());

                    result.add(characteristic);
                }
            }
        }

        return result;
    }

    /**
     * 解析PhysicalSystem对象，并加以处理，最后返回自定义Contact对象集合
     * @param
     * @return
     */
    public List<Contact> getContact(InsertSensorRequest insertSensorRequest) {
        List<Contact> result = new ArrayList<>();

        OgcPropertyList<ContactList> contactLists = insertSensorRequest.getProcedureDescription().getContactsList();
        if (contactLists!=null && contactLists.size()>0) {
            OgcPropertyList<CIResponsibleParty> contactList = contactLists.get(0).getContactList();
            if (contactList!=null && contactList.size()>0) {
                for (int i=0; i<contactList.size(); i++) {
                    Contact contact = new Contact();
                    Address address = new Address();
                    Telephone telephone = new Telephone();
                    contact.setIdentifier(insertSensorRequest.getProcedureDescription().getUniqueIdentifier()+":Contact_No"+(i+1));
                    if (org.apache.commons.lang3.StringUtils.isBlank(contactList.get(i).getIndividualName())) {
                        contact.setIndividualName(contactList.get(i).getIndividualName());
                    }
                    if (org.apache.commons.lang3.StringUtils.isBlank(contactList.get(i).getOrganisationName())) {
                        contact.setOrganizationName(contactList.get(i).getOrganisationName());
                    }
                    if (org.apache.commons.lang3.StringUtils.isBlank(contactList.get(i).getPositionName())) {
                        contact.setPositionName(contactList.get(i).getPositionName());
                    }
                    if (org.apache.commons.lang3.StringUtils.isBlank(contactList.get(i).getRole().toString())) {
                        contact.setRole(contactList.get(i).getRole().getValue());
                    }
                    //address info
                    CIAddress addressTemp = contactList.get(i).getContactInfo().getAddress();
                    if (addressTemp!=null) {
                        if (org.apache.commons.lang3.StringUtils.isBlank(addressTemp.getAdministrativeArea())) {
                            address.setAdministrativeArea(addressTemp.getAdministrativeArea());
                        }
                        if (org.apache.commons.lang3.StringUtils.isBlank(addressTemp.getCity())) {
                            address.setCity(addressTemp.getCity());
                        }
                        if (org.apache.commons.lang3.StringUtils.isBlank(addressTemp.getCountry())) {
                            address.setCountry(addressTemp.getCountry());
                        }
                        if (org.apache.commons.lang3.StringUtils.isBlank(addressTemp.getPostalCode())) {
                            address.setPostalCode(addressTemp.getPostalCode());
                        }
                        if (addressTemp.getDeliveryPointList()!=null && addressTemp.getDeliveryPointList().size()>0) {
                            address.setDeliveryPoint(DataCenterUtils.list2String(addressTemp.getDeliveryPointList()));
                        }
                        if (addressTemp.getElectronicMailAddressList()!=null && addressTemp.getElectronicMailAddressList().size()>0) {
                            address.setElectronicMailAddress(DataCenterUtils.list2String(addressTemp.getElectronicMailAddressList()));
                        }
                        address.setIdentifier(insertSensorRequest.getProcedureDescription().getUniqueIdentifier()+":Contact_No"+(i+1)+":Address_No"+(i+1));
                        contact.setAddress(address);
                    }
                    //phone info
                    CITelephone phoneTemp = contactList.get(i).getContactInfo().getPhone();
                    if (phoneTemp!=null) {
                        if (phoneTemp.getVoiceList()!=null && phoneTemp.getVoiceList().size()>0) {
                            telephone.setVoice(DataCenterUtils.list2String(phoneTemp.getVoiceList()));
                        }
                        if (phoneTemp.getFacsimileList()!=null && phoneTemp.getFacsimileList().size()>0) {
                            telephone.setFacsimile(DataCenterUtils.list2String(phoneTemp.getFacsimileList()));
                        }
                        telephone.setIdentifier(insertSensorRequest.getProcedureDescription().getUniqueIdentifier()+":Contact_No"+(i+1)+":Telephone_No"+(i+1));
                        contact.setTelephone(telephone);
                    }
                    result.add(contact);
                }
            }
        }

        return result;
    }

}

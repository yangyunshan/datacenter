package com.sensorweb.datacenter.service.sos;

import com.sensorweb.datacenter.dao.*;
import com.sensorweb.datacenter.entity.sos.*;
import com.sensorweb.datacenter.util.DataCenterUtils;
import net.opengis.OgcProperty;
import net.opengis.OgcPropertyList;
import net.opengis.sensorml.v20.*;
import net.opengis.sensorml.v20.Event;
import net.opengis.swe.v20.DataComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.analysis.function.Add;
import org.isotc211.v2005.gmd.CIAddress;
import org.isotc211.v2005.gmd.CIResponsibleParty;
import org.isotc211.v2005.gmd.CITelephone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.vast.ows.OWSException;
import org.vast.ows.sos.InsertSensorReaderV20;
import org.vast.ows.sos.InsertSensorRequest;
import org.vast.ows.sos.InsertSensorResponse;
import org.vast.ows.sos.InsertSensorWriterV20;
import org.vast.ows.swe.InsertSensorResponseWriterV20;
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
import java.util.Map;
import java.util.Set;


@Service
public class InsertSensorService {

    @Autowired
    private ProcedureMapper procedureMapper;

    @Autowired
    private OfferingMapper offeringMapper;

    @Autowired
    private ProcOffMapper procOffMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private CapabilityMapper capabilityMapper;

    @Autowired
    private CharacteristicMapper characteristicMapper;

    @Autowired
    private ClassificationMapper classificationMapper;

    @Autowired
    private IdentificationMapper identificationMapper;

    @Autowired
    private KeywordMapper keywordMapper;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private TelephoneMapper telephoneMapper;

    @Autowired
    private ValidTimeMapper validTimeMapper;

    @Value("${datacenter.path.upload}")
    private String uploadPath;


    /**
     * 生成InsertSensor响应文档
     * @param procedureId
     * @param
     * @return
     */
    public Element getInsertSensorResponse(String procedureId) throws OWSException {
        InsertSensorResponse response = new InsertSensorResponse();
        response.setAssignedOffering("");
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
        return insertSensorReader.readXMLQuery(domHelper, domHelper.getRootElement());
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
    public String insertSensor(InsertSensorRequest insertSensorRequest) throws Exception {
        String result = "";

        //add identification
        List<Identification> identifications = getIdentification(insertSensorRequest);
        if (identifications!=null && identifications.size()>0) {
            for (Identification identification : identifications) {
                identificationMapper.insertData(identification);
            }
        }

        //add classification
        List<Classification> classifications = getClassification(insertSensorRequest);
        if (classifications!=null && classifications.size()>0) {
            for (Classification classification : classifications) {
                classificationMapper.insertData(classification);
            }
        }

        //add characteristic
        List<Characteristic> characteristics = getCharacteristic(insertSensorRequest);
        if (characteristics!=null && characteristics.size()>0) {
            for (Characteristic characteristic : characteristics) {
                characteristicMapper.insertData(characteristic);
            }
        }

        //add capability
        List<Capability> capabilities = getCapability(insertSensorRequest);
        if (capabilities!=null && capabilities.size()>0) {
            for (Capability capability : capabilities) {
                capabilityMapper.insertData(capability);
            }
        }

        //add validTime
        ValidTime validTime = getValidTime(insertSensorRequest);
        if (validTime!=null) {
            validTimeMapper.insertData(validTime);
        }

        //add contact
        List<Contact> contacts = getContact(insertSensorRequest);
        if (contacts!=null && contacts.size()>0) {
            for (Contact contact : contacts) {
                Address address = contact.getAddress();
                if (address!=null) {
                    addressMapper.insertData(address);
                }
                Telephone telephone = contact.getTelephone();
                if (telephone!=null) {
                    telephoneMapper.insertData(telephone);
                }
                contactMapper.insertData(contact);
            }
        }

        //add position
        List<Position> positions = getPosition(insertSensorRequest);
        if (positions!=null && positions.size()>0) {
            for (Position position : positions) {
                positionMapper.insertData(position);
            }
        }

        //add keywords
        Keyword keyword = getKeyword(insertSensorRequest);
        if (keyword!=null) {
            keywordMapper.insertData(keyword);
        }

        //add procedure
        Procedure procedure = getProcedure(insertSensorRequest);
        if (procedure!=null) {
            try {
                int flag = procedureMapper.insertData(procedure);
                if (flag>0) {
                    result = procedure.getId();
                }
            } catch (Exception e) {
                new File(procedure.getDescriptionFile()).delete();
                throw new Exception("add failure");
            }

        }

        return result;
    }

    /**
     * 解析Request请求，获取关键字信息
     * @param insertSensorRequest
     * @return
     */
    public Keyword getKeyword(InsertSensorRequest insertSensorRequest) {
        Keyword keywords = new Keyword();
        String identifier = insertSensorRequest.getProcedureDescription().getUniqueIdentifier();
        keywords.setProcedureId(identifier);

        OgcPropertyList<KeywordList> keywordLists = insertSensorRequest.getProcedureDescription().getKeywordsList();
        if (keywordLists!=null && keywordLists.size()>0) {
            List<String> keywordTemp = keywordLists.get(0).getKeywordList();
            if (keywordTemp!=null && keywordTemp.size()>0) {
                keywords.setValues(keywordTemp);
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
     * 解析InsertSensorRequest请求，获取Offering信息，暂未存储offering与procedure的映射关系
     * @param request
     * @return
     */
    public List<Offering> getOfferings(InsertSensorRequest request) {
        List<Offering> result = new ArrayList<>();
        if (request!=null) {
            AbstractProcess abstractProcess = request.getProcedureDescription();
            if (abstractProcess!=null) {
                Set<String> name = abstractProcess.getCapabilitiesList().getPropertyNames();
                if (!name.contains("offerings")) {
                    return null;
                }
                CapabilityList offeringCap = abstractProcess.getCapabilities("offerings");
                if (offeringCap!=null && offeringCap.getCapabilityList()!=null && offeringCap.getCapabilityList().size()>0) {
                    OgcPropertyList<DataComponent> datas = offeringCap.getCapabilityList();
                    for (int i=0; i<datas.size();i++) {
                        Offering temp = new Offering();
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
     * 解析Request对象，并加以处理，最后返回自定义Identification对象集合
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
                    identification.setProcedureId(insertSensorRequest.getProcedureDescription().getUniqueIdentifier());
                    identification.setLabel(identifiers.get(i).getLabel());
                    identification.setValue(identifiers.get(i).getValue());

                    result.add(identification);
                }
            }
        }

        return result;
    }

    /**
     * 解析Request对象，并加以处理，最后返回自定义Classification对象集合
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
                    classification.setProcedureId(insertSensorRequest.getProcedureDescription().getUniqueIdentifier() + ":Classification_No"+(i+1));
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
     * 解析Request对象，并加以处理，最后返回自定义ValidTime对象集合
     * @param
     * @return
     * @throws ParseException
     */
    public ValidTime getValidTime(InsertSensorRequest insertSensorRequest) throws ParseException {
        ValidTime result = new ValidTime();

        TimeExtent timeExtent = insertSensorRequest.getProcedureDescription().getValidTime();
        if (timeExtent!=null) {
            result.setProcedureId(insertSensorRequest.getProcedureDescription().getUniqueIdentifier());
            if (timeExtent.begin()!=null) {

                result.setBeginPosition(timeExtent.begin());
            }
            if (timeExtent.end()!=null) {
                result.setEndPosition(timeExtent.end());
            }
        }

        return result;
    }

    /**
     * 解析Request对象，并加以处理，最后返回自定义Capability对象集合
     * @param
     * @return
     */
    public List<Capability> getCapability(InsertSensorRequest insertSensorRequest) {
        List<Capability> result = new ArrayList<>();

        OgcPropertyList<CapabilityList> capabilityLists = insertSensorRequest.getProcedureDescription().getCapabilitiesList();
        if (capabilityLists!=null && capabilityLists.size()>0) {
            Set<String> capNames = capabilityLists.getPropertyNames();
            for (String capName : capNames) {
                if (!capName.equals("offerings")) {
                    OgcPropertyList<DataComponent> capabilityList = capabilityLists.get(capName).getCapabilityList();
                    List<OgcProperty<DataComponent>> data = capabilityList.getProperties();
                    for (OgcProperty<DataComponent> capability : data) {
                        Capability temp = new Capability();
                        temp.setProcedureId(insertSensorRequest.getProcedureDescription().getUniqueIdentifier());
                        temp.setName(capability.getName());
                        temp.setValue(capability.getValue().toString());

                        result.add(temp);
                    }
                }
            }

        }

        return result;
    }

    /**
     * 解析Request对象，并加以处理，最后返回自定义Characteristic对象集合
     * @param
     * @return
     */
    public List<Characteristic> getCharacteristic(InsertSensorRequest insertSensorRequest) {
        List<Characteristic> result = new ArrayList<>();

        OgcPropertyList<CharacteristicList> characteristicLists = insertSensorRequest.getProcedureDescription().getCharacteristicsList();

        if (characteristicLists!=null && characteristicLists.size()>0) {
            OgcPropertyList<DataComponent> characteristicList = characteristicLists.get(0).getCharacteristicList();
            List<OgcProperty<DataComponent>> data = characteristicList.getProperties();
            for (OgcProperty<DataComponent> characteristic : data) {
                Characteristic temp = new Characteristic();
                temp.setProcedureId(insertSensorRequest.getProcedureDescription().getUniqueIdentifier());
                temp.setName(characteristic.getName());
                temp.setValue(characteristic.getValue().toString());

                result.add(temp);
            }
        }

        return result;
    }

    /**
     * 解析Request对象，并加以处理，最后返回自定义Contact对象集合
     * @param
     * @return
     */
    public List<Contact> getContact(InsertSensorRequest insertSensorRequest) {
        List<Contact> result = new ArrayList<>();

        OgcPropertyList<ContactList> contactLists = insertSensorRequest.getProcedureDescription().getContactsList();
        if (contactLists!=null && contactLists.size()>0) {
            OgcPropertyList<CIResponsibleParty> contactList = contactLists.get(0).getContactList();
            if (contactList!=null) {
                List<OgcProperty<CIResponsibleParty>> contacts =contactList.getProperties();

                for (OgcProperty<CIResponsibleParty> contact : contacts) {
                    Contact contactTemp = new Contact();
                    Address address = new Address();
                    Telephone telephone = new Telephone();
                    contactTemp.setProcedureId(insertSensorRequest.getProcedureDescription().getUniqueIdentifier());

                    if (!StringUtils.isBlank(contact.getTitle())) {
                        contactTemp.setTitle(contact.getTitle());
                    }
                    if (!StringUtils.isBlank(contact.getValue().getIndividualName())) {
                        contactTemp.setIndividualName(contact.getValue().getIndividualName());
                    }
                    if (!StringUtils.isBlank(contact.getValue().getOrganisationName())) {
                        contactTemp.setOrganizationName(contact.getValue().getOrganisationName());
                    }
                    if (!StringUtils.isBlank(contact.getValue().getPositionName())) {
                        contactTemp.setPositionName(contact.getValue().getPositionName());
                    }
                    if (contact.getValue().getRole()!=null && !StringUtils.isBlank(contact.getValue().getRole().getValue())) {
                        contactTemp.setRole(contact.getValue().getRole().getValue());
                    }
                    //address info
                    CIAddress addressTemp = contact.getValue().getContactInfo().getAddress();
                    if (addressTemp!=null) {
                        if (!StringUtils.isBlank(addressTemp.getAdministrativeArea())) {
                            address.setAdministrativeArea(addressTemp.getAdministrativeArea());
                        }
                        if (!StringUtils.isBlank(addressTemp.getCity())) {
                            address.setCity(addressTemp.getCity());
                        }
                        if (!StringUtils.isBlank(addressTemp.getCountry())) {
                            address.setCountry(addressTemp.getCountry());
                        }
                        if (!StringUtils.isBlank(addressTemp.getPostalCode())) {
                            address.setPostalCode(addressTemp.getPostalCode());
                        }
                        if (addressTemp.getDeliveryPointList()!=null && addressTemp.getDeliveryPointList().size()>0) {
                            address.setDeliveryPoint(DataCenterUtils.list2String(addressTemp.getDeliveryPointList()));
                        }
                        if (addressTemp.getElectronicMailAddressList()!=null && addressTemp.getElectronicMailAddressList().size()>0) {
                            address.setElectronicMailAddress(DataCenterUtils.list2String(addressTemp.getElectronicMailAddressList()));
                        }
                        address.setProcedureId(insertSensorRequest.getProcedureDescription().getUniqueIdentifier());
                        contactTemp.setAddress(address);
                    }
                    //phone info
                    CITelephone phoneTemp = contact.getValue().getContactInfo().getPhone();
                    if (phoneTemp!=null) {
                        if (phoneTemp.getVoiceList()!=null && phoneTemp.getVoiceList().size()>0) {
                            telephone.setVoice(DataCenterUtils.list2String(phoneTemp.getVoiceList()));
                        }
                        if (phoneTemp.getFacsimileList()!=null && phoneTemp.getFacsimileList().size()>0) {
                            telephone.setFacsimile(DataCenterUtils.list2String(phoneTemp.getFacsimileList()));
                        }
                        telephone.setProcedureId(insertSensorRequest.getProcedureDescription().getUniqueIdentifier());
                        contactTemp.setTelephone(telephone);
                    }
                    result.add(contactTemp);
                    contact.getValue().getIndividualName();
                }
//                for (int i=0; i<contactList.size(); i++) {
//
//                }
            }
        }

        return result;
    }

    /**
     * 解析Request对象，并加以处理，最后返回自定义Position对象集合
     * @param insertSensorRequest
     * @return
     */
    public List<Position> getPosition(InsertSensorRequest insertSensorRequest) {
        List<Position> res = new ArrayList<>();

        AbstractPhysicalProcess abstractProcess = (AbstractPhysicalProcess) insertSensorRequest.getProcedureDescription();
        OgcPropertyList<Serializable> positionList = abstractProcess.getPositionList();
        if (positionList!=null && positionList.size()>0) {
            for (int i=0; i<positionList.size(); i++) {
                Position temp = new Position();
                temp.setProcedureId(insertSensorRequest.getProcedureDescription().getUniqueIdentifier());
                Map<String, String> prop = DataCenterUtils.string2Map(positionList.getProperty(i).getValue().toString());
                Set<String> keys = prop.keySet();
                if (keys.contains("Text")) {
                    temp.setName(prop.get("Text"));
                }
                if (keys.contains("Lat")) {
                    temp.setLatitude(Double.parseDouble(prop.get("Lat")));
                }
                if (keys.contains("Lon")) {
                    temp.setLongitude(Double.parseDouble(prop.get("Lon")));
                }
                if (keys.contains("Alt")) {
                    temp.setAltitude(Double.parseDouble(prop.get("Alt")));
                }

                res.add(temp);
            }
        }
        return res;
    }

    /**
     * 获得History信息(暂未完全实现)
     * @param insertSensorRequest
     * @return
     */
    public List<com.sensorweb.datacenter.entity.sos.Event> getEvent(InsertSensorRequest insertSensorRequest) {
        List<com.sensorweb.datacenter.entity.sos.Event> res = new ArrayList<>();

        OgcPropertyList<EventList> historyList = insertSensorRequest.getProcedureDescription().getHistoryList();
        if (historyList!=null) {
            OgcPropertyList<Event> histories = historyList.get(0).getEventList();
            if (histories!=null) {
                List<OgcProperty<Event>> events = histories.getProperties();
                OgcProperty<Event> ss = events.get(0);
                System.out.println();
            }
        }

        return res;
    }


}

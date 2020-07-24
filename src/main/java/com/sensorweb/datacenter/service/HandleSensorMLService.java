package com.sensorweb.datacenter.service;

import com.sensorweb.datacenter.dao.*;
import com.sensorweb.datacenter.entity.*;
import com.sensorweb.datacenter.entity.Event;
import com.sensorweb.datacenter.util.DataCenterUtils;
import net.opengis.OgcPropertyList;
import net.opengis.gml.v32.impl.GMLFactory;
import net.opengis.sensorml.v20.*;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.Factory;
import org.isotc211.v2005.gco.impl.GCOFactory;
import org.isotc211.v2005.gmd.CIAddress;
import org.isotc211.v2005.gmd.CIResponsibleParty;
import org.isotc211.v2005.gmd.CITelephone;
import org.isotc211.v2005.gmd.impl.GMDFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.vast.data.SWEFactory;
import org.vast.sensorML.SMLFactory;
import org.vast.util.TimeExtent;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HandleSensorMLService {

    @Autowired
    private ProcedureMapper procedureMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private CapabilityMapper capabilityMapper;

    @Autowired
    private CharacteristicMapper characteristicMapper;

    @Autowired
    private ClassificationMapper classificationMapper;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private IdentificationMapper identificationMapper;

    @Autowired
    private KeywordMapper keywordMapper;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private  TelephoneMapper telephoneMapper;

    @Autowired
    private ValidTimeMapper validTimeMapper;

    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean storePhysicalSystemData(String sensorml, String fileLocation) throws ParseException, XMLStreamException {
        PhysicalSystem physicalSystem = parseSensorML(sensorml);

        //add keywords
        keywordMapper.insertData(getKeyword(physicalSystem));

        //add identifier
        List<Identification> identifications = getIdentification(physicalSystem);
        for (Identification identification : identifications) {
            identificationMapper.insertData(identification);
        }

        //add classifier
        List<Classification> classifications = getClassification(physicalSystem);
        for (Classification classification : classifications) {
            classificationMapper.insertData(classification);
        }

        //add validtime
        validTimeMapper.insertData(getValidTime(physicalSystem));

        //add characteristic
        List<Characteristic> characteristics = getCharacteristic(physicalSystem);
        for (Characteristic characteristic : characteristics) {
            characteristicMapper.insertData(characteristic);
        }

        //add capability
        List<Capability> capabilities = getCapability(physicalSystem);
        for (Capability capability : capabilities) {
            capabilityMapper.insertData(capability);
        }

        //add contact
        List<Contact> contacts = getContact(physicalSystem);
        for (Contact contact : contacts) {
            addressMapper.insertData(contact.getAddress());
            telephoneMapper.insertData(contact.getTelephone());
            contactMapper.insertData(contact);
        }

        //add procedure
        procedureMapper.insertData(getProcedure(physicalSystem, fileLocation));

        return true;
    }



    /**
     * 解析PhysicalSystem类型sensorML数据，返回PhysicalSystem对象
     * @param sensorml
     * @return
     */
    public PhysicalSystem parseSensorML(String sensorml) throws XMLStreamException {
        PhysicalSystem physicalSystem = null;

        Factory sweFactory = new SWEFactory();
        net.opengis.gml.v32.Factory gmlFactory = new GMLFactory();
        org.isotc211.v2005.gmd.Factory gmdFactory = new GMDFactory();
        org.isotc211.v2005.gco.Factory gcoFactory = new GCOFactory();
        net.opengis.sensorml.v20.Factory sensormlFactory = new SMLFactory();

        net.opengis.sensorml.v20.bind.XMLStreamBindings smlXMLStreamBindings = new net.opengis.sensorml.v20.bind.XMLStreamBindings(sensormlFactory,sweFactory,gmlFactory,gmdFactory,gcoFactory);

        XMLInputFactory factory = XMLInputFactory.newInstance();

        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(sensorml));
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (reader.getLocalName().equals("PhysicalSystem")) {
                    physicalSystem = smlXMLStreamBindings.readPhysicalSystem(reader);
                }
            }
        }

        return physicalSystem;
    }

    /**
     * 解析PhysicalSystem对象，并加以处理，最后返回自定义Keyword对象
     * @param physicalSystem
     * @return
     */
    public Keyword getKeyword(PhysicalSystem physicalSystem) {
        Keyword keywords = new Keyword();
        String identifier = physicalSystem.getUniqueIdentifier();
        keywords.setIdentifier(identifier + ":keywords");

        KeywordList keywordsList = physicalSystem.getKeywordsList().get(0);
        List<String> temp = keywordsList.getKeywordList();
        if (temp!=null && temp.size()>0) {
            keywords.setValues(DataCenterUtils.list2String(temp));
        }

        return keywords;
    }

    /**
     * 解析PhysicalSystem对象，并加以处理，最后返回自定义Identification对象集合
     * @param physicalSystem
     * @return
     */
    public List<Identification> getIdentification(PhysicalSystem physicalSystem) {
        List<Identification> result = new ArrayList<>();

        OgcPropertyList<IdentifierList> identifierLists = physicalSystem.getIdentificationList();
        if (identifierLists!=null && identifierLists.size()>0) {
            List<Term> identifiers = identifierLists.get(0).getIdentifierList();
            if (identifiers!=null && identifiers.size()>0) {
                for (int i = 0; i<identifiers.size(); i++) {
                    Identification identification = new Identification();
                    identification.setDefinition(identifiers.get(i).getDefinition());
                    identification.setIdentifier(physicalSystem.getUniqueIdentifier() + ":Identification_No" + (i+1));
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
     * @param physicalSystem
     * @return
     */
    public List<Classification> getClassification(PhysicalSystem physicalSystem) {
        List<Classification> result = new ArrayList<>();

        OgcPropertyList<ClassifierList> classifierLists = physicalSystem.getClassificationList();
        if (classifierLists!=null && classifierLists.size()>0) {
            List<Term> terms = classifierLists.get(0).getClassifierList();
            if (terms!=null && terms.size()>0) {
                for (int i=0; i<terms.size(); i++) {
                    Classification classification = new Classification();
                    classification.setIdentifier(physicalSystem.getUniqueIdentifier() + ":Classification_No"+(i+1));
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
     * @param physicalSystem
     * @return
     * @throws ParseException
     */
    public ValidTime getValidTime(PhysicalSystem physicalSystem) throws ParseException {
        ValidTime result = new ValidTime();

        TimeExtent timeExtent = physicalSystem.getValidTime();
        if (timeExtent!=null) {
            result.setIdentifier(physicalSystem.getUniqueIdentifier()+":ValidTime");
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
     * @param physicalSystem
     * @return
     */
    public List<Capability> getCapability(PhysicalSystem physicalSystem) {
        List<Capability> result = new ArrayList<>();

        OgcPropertyList<CapabilityList> capabilityLists = physicalSystem.getCapabilitiesList();
        if (capabilityLists!=null && capabilityLists.size()>0) {
            OgcPropertyList<DataComponent> capabilityList = capabilityLists.get(0).getCapabilityList();
            if (capabilityList!=null && capabilityList.size()>0) {
                for (int i=0; i<capabilityList.size(); i++) {
                    Capability capability = new Capability();
                    capability.setIdentifier(physicalSystem.getUniqueIdentifier()+":Capability_No"+(i+1));
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
     * @param physicalSystem
     * @return
     */
    public List<Characteristic> getCharacteristic(PhysicalSystem physicalSystem) {
        List<Characteristic> result = new ArrayList<>();

        OgcPropertyList<CharacteristicList> characteristicLists = physicalSystem.getCharacteristicsList();
        if (characteristicLists!=null && characteristicLists.size()>0) {
            OgcPropertyList<DataComponent> characteristicList = characteristicLists.get(0).getCharacteristicList();
            if (characteristicList!=null && characteristicList.size()>0) {
                for (int i = 0;i<characteristicList.size(); i++) {
                    Characteristic characteristic = new Characteristic();
                    characteristic.setIdentifier(physicalSystem.getUniqueIdentifier()+":Characteristic_No"+(i+1));
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
     * @param physicalSystem
     * @return
     */
    public List<Contact> getContact(PhysicalSystem physicalSystem) {
        List<Contact> result = new ArrayList<>();

        OgcPropertyList<ContactList> contactLists = physicalSystem.getContactsList();
        if (contactLists!=null && contactLists.size()>0) {
            OgcPropertyList<CIResponsibleParty> contactList = contactLists.get(0).getContactList();
            if (contactList!=null && contactList.size()>0) {
                for (int i=0; i<contactList.size(); i++) {
                    Contact contact = new Contact();
                    Address address = new Address();
                    Telephone telephone = new Telephone();
                    contact.setIdentifier(physicalSystem.getUniqueIdentifier()+":Contact_No"+(i+1));
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
                        address.setIdentifier(physicalSystem.getUniqueIdentifier()+":Contact_No"+(i+1)+":Address_No"+(i+1));
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
                        telephone.setIdentifier(physicalSystem.getUniqueIdentifier()+":Contact_No"+(i+1)+":Telephone_No"+(i+1));
                        contact.setTelephone(telephone);
                    }
                    result.add(contact);
                }
            }
        }

        return result;
    }

    /**
     * 后续实现
     * @param physicalSystem
     * @return
     */
    public List<Event> getEvent(PhysicalSystem physicalSystem) {
        List<Event> result = new ArrayList<>();

        OgcPropertyList<EventList> eventLists = physicalSystem.getHistoryList();
        if (eventLists!=null && eventLists.size()>0) {
            OgcPropertyList<net.opengis.sensorml.v20.Event> eventList = eventLists.get(0).getEventList();
            if (eventList!=null && eventList.size()>0) {
                for (int i=0; i<eventList.size(); i++) {
                    Event event = new Event();
                    event.setIdentifier(physicalSystem.getUniqueIdentifier()+":Event_No"+(i+1));

                }
            }
        }

        return result;
    }

    /**
     * 后续实现
     * @param physicalSystem
     * @return
     */
    public Position getPosition(PhysicalSystem physicalSystem) {
        Position position = new Position();

        physicalSystem.getPositionList().getPropertyNames();
        return position;
    }

    /**
     * 解析PhysicalSystem对象，并加以处理，最后返回自定义Procedure对象集合
     * @param physicalSystem
     * @param url
     * @return
     */
    public Procedure getProcedure(PhysicalSystem physicalSystem, String url) {
        Procedure procedure = new Procedure();

        if (physicalSystem!=null) {
            procedure.setId(physicalSystem.getUniqueIdentifier());
            procedure.setDescription(physicalSystem.getDescription());
            procedure.setDescriptionFile(url);
            procedure.setDescriptionFormat(physicalSystem.getQName().getNamespaceURI());
//            procedure.setTypeOf(physicalSystem.getTypeOf().getHref());
        }
        return procedure;
    }
}

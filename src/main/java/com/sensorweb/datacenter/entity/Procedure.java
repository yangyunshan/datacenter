package com.sensorweb.datacenter.entity;


public class Procedure {
    private int procedureId;
    private String procedureDescriptionFormat;
    private String identifier;
    private String name;
    private String description;
    private char disabled = 'F';
    private String descriptionFile;
    private String typeOf;
    private char isType = 'F';
    private char isAggregation = 'F';
    private char isMobile = 'F';
    private char isInsitu = 'F';

    public int getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(int procedureId) {
        this.procedureId = procedureId;
    }

    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    public void setProcedureDescriptionFormat(String procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public char getDisabled() {
        return disabled;
    }

    public void setDisabled(char disabled) {
        this.disabled = disabled;
    }

    public String getDescriptionFile() {
        return descriptionFile;
    }

    public void setDescriptionFile(String descriptionFile) {
        this.descriptionFile = descriptionFile;
    }

    public String getTypeOf() {
        return typeOf;
    }

    public void setTypeOf(String typeOf) {
        this.typeOf = typeOf;
    }

    public char getIsAggregation() {
        return isAggregation;
    }

    public void setIsAggregation(char isAggregation) {
        this.isAggregation = isAggregation;
    }

    public char getIsInsitu() {
        return isInsitu;
    }

    public void setIsInsitu(char isInsitu) {
        this.isInsitu = isInsitu;
    }

    public char getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(char isMobile) {
        this.isMobile = isMobile;
    }

    public char getIsType() {
        return isType;
    }

    public void setIsType(char isType) {
        this.isType = isType;
    }
}

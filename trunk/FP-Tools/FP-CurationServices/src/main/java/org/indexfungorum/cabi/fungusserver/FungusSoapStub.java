/**
 * FungusSoapStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class FungusSoapStub extends org.apache.axis.client.Stub implements org.indexfungorum.cabi.fungusserver.FungusSoap {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[20];
        _initOperationDesc1();
        _initOperationDesc2();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("IsAlive");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        oper.setReturnClass(boolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "IsAliveResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("NameSearch");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "SearchText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AnywhereInText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "MaxNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameSearchResponse>NameSearchResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.NameSearchResponseNameSearchResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameSearchResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("NameSearchDs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "SearchText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AnywhereInText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "MaxNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameSearchDsResponse>NameSearchDsResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.NameSearchDsResponseNameSearchDsResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameSearchDsResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("EpithetSearch");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "SearchText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AnywhereInText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "MaxNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>EpithetSearchResponse>EpithetSearchResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.EpithetSearchResponseEpithetSearchResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "EpithetSearchResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("EpithetSearchDs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "SearchText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AnywhereInText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "MaxNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>EpithetSearchDsResponse>EpithetSearchDsResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponseEpithetSearchDsResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "EpithetSearchDsResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("NameByKey");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameByKeyResponse>NameByKeyResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.NameByKeyResponseNameByKeyResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameByKeyResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("NameByKeyDs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameByKeyDsResponse>NameByKeyDsResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.NameByKeyDsResponseNameByKeyDsResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameByKeyDsResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("NameFullByKey");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameLsid"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameFullByKeyResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("NameByKeyRDF");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameLsid"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameByKeyRDFResponse>NameByKeyRDFResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.NameByKeyRDFResponseNameByKeyRDFResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameByKeyRDFResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("AuthorSearch");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "SearchText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AnywhereInText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "MaxNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>AuthorSearchResponse>AuthorSearchResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.AuthorSearchResponseAuthorSearchResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AuthorSearchResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("AuthorSearchDs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "SearchText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AnywhereInText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "MaxNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>AuthorSearchDsResponse>AuthorSearchDsResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponseAuthorSearchDsResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AuthorSearchDsResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("NamesByCurrentKey");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "CurrentKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NamesByCurrentKeyResponse>NamesByCurrentKeyResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyResponseNamesByCurrentKeyResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NamesByCurrentKeyResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("NamesByCurrentKeyDs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "CurrentKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NamesByCurrentKeyDsResponse>NamesByCurrentKeyDsResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponseNamesByCurrentKeyDsResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NamesByCurrentKeyDsResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("AllUpdatedNames");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>AllUpdatedNamesResponse>AllUpdatedNamesResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponseAllUpdatedNamesResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AllUpdatedNamesResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("UpdatedNamesInRange");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "rank"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>UpdatedNamesInRangeResponse>UpdatedNamesInRangeResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponseUpdatedNamesInRangeResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "UpdatedNamesInRangeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("UpdatedNames");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "rank"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>UpdatedNamesResponse>UpdatedNamesResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.UpdatedNamesResponseUpdatedNamesResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "UpdatedNamesResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("NewNamesInRange");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "rank"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NewNamesInRangeResponse>NewNamesInRangeResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponseNewNamesInRangeResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NewNamesInRangeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("NewNames");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "rank"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NewNamesResponse>NewNamesResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.NewNamesResponseNewNamesResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NewNamesResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DeprecatedNames");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>DeprecatedNamesResponse>DeprecatedNamesResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponseDeprecatedNamesResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "DeprecatedNamesResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DeprecatedNamesByRank");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "rank"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>DeprecatedNamesByRankResponse>DeprecatedNamesByRankResult"));
        oper.setReturnClass(org.indexfungorum.cabi.fungusserver.DeprecatedNamesByRankResponseDeprecatedNamesByRankResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "DeprecatedNamesByRankResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[19] = oper;

    }

    public FungusSoapStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public FungusSoapStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public FungusSoapStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>AllUpdatedNamesResponse>AllUpdatedNamesResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponseAllUpdatedNamesResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>AuthorSearchDsResponse>AuthorSearchDsResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponseAuthorSearchDsResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>AuthorSearchResponse>AuthorSearchResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.AuthorSearchResponseAuthorSearchResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>DeprecatedNamesByRankResponse>DeprecatedNamesByRankResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.DeprecatedNamesByRankResponseDeprecatedNamesByRankResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>DeprecatedNamesResponse>DeprecatedNamesResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponseDeprecatedNamesResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>EpithetSearchDsResponse>EpithetSearchDsResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponseEpithetSearchDsResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>EpithetSearchResponse>EpithetSearchResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.EpithetSearchResponseEpithetSearchResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameByKeyDsResponse>NameByKeyDsResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameByKeyDsResponseNameByKeyDsResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameByKeyRDFResponse>NameByKeyRDFResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameByKeyRDFResponseNameByKeyRDFResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameByKeyResponse>NameByKeyResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameByKeyResponseNameByKeyResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NamesByCurrentKeyDsResponse>NamesByCurrentKeyDsResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponseNamesByCurrentKeyDsResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NamesByCurrentKeyResponse>NamesByCurrentKeyResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyResponseNamesByCurrentKeyResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameSearchDsResponse>NameSearchDsResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameSearchDsResponseNameSearchDsResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameSearchResponse>NameSearchResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameSearchResponseNameSearchResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NewNamesInRangeResponse>NewNamesInRangeResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponseNewNamesInRangeResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NewNamesResponse>NewNamesResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NewNamesResponseNewNamesResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>UpdatedNamesInRangeResponse>UpdatedNamesInRangeResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponseUpdatedNamesInRangeResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>UpdatedNamesResponse>UpdatedNamesResult");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.UpdatedNamesResponseUpdatedNamesResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">AllUpdatedNames");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.AllUpdatedNames.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">AllUpdatedNamesResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">AuthorSearch");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.AuthorSearch.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">AuthorSearchDs");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.AuthorSearchDs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">AuthorSearchDsResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">AuthorSearchResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.AuthorSearchResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">DataSet");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.DataSet.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">DeprecatedNames");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.DeprecatedNames.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">DeprecatedNamesByRank");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.DeprecatedNamesByRank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">DeprecatedNamesByRankResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.DeprecatedNamesByRankResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">DeprecatedNamesResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">EpithetSearch");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.EpithetSearch.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">EpithetSearchDs");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.EpithetSearchDs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">EpithetSearchDsResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">EpithetSearchResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.EpithetSearchResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameByKey");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameByKey.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameByKeyDs");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameByKeyDs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameByKeyDsResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameByKeyDsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameByKeyRDF");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameByKeyRDF.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameByKeyRDFResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameByKeyRDFResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameByKeyResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameByKeyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameFullByKey");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameFullByKey.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameFullByKeyResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameFullByKeyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NamesByCurrentKey");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NamesByCurrentKey.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NamesByCurrentKeyDs");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NamesByCurrentKeyDsResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NamesByCurrentKeyResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameSearchDs");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameSearchDs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameSearchDsResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NameSearchDsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NewNames");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NewNames.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NewNamesInRange");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NewNamesInRange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NewNamesInRangeResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NewNamesResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.NewNamesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">UpdatedNames");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.UpdatedNames.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">UpdatedNamesInRange");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.UpdatedNamesInRange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">UpdatedNamesInRangeResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">UpdatedNamesResponse");
            cachedSerQNames.add(qName);
            cls = org.indexfungorum.cabi.fungusserver.UpdatedNamesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public boolean isAlive() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/IsAlive");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "IsAlive"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Boolean) _resp).booleanValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_resp, boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.NameSearchResponseNameSearchResult nameSearch(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/NameSearch");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameSearch"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {searchText, new java.lang.Boolean(anywhereInText), new java.lang.Long(maxNumber)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.NameSearchResponseNameSearchResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.NameSearchResponseNameSearchResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.NameSearchResponseNameSearchResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.NameSearchDsResponseNameSearchDsResult nameSearchDs(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/NameSearchDs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameSearchDs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {searchText, new java.lang.Boolean(anywhereInText), new java.lang.Long(maxNumber)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.NameSearchDsResponseNameSearchDsResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.NameSearchDsResponseNameSearchDsResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.NameSearchDsResponseNameSearchDsResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.EpithetSearchResponseEpithetSearchResult epithetSearch(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/EpithetSearch");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "EpithetSearch"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {searchText, new java.lang.Boolean(anywhereInText), new java.lang.Long(maxNumber)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.EpithetSearchResponseEpithetSearchResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.EpithetSearchResponseEpithetSearchResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.EpithetSearchResponseEpithetSearchResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponseEpithetSearchDsResult epithetSearchDs(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/EpithetSearchDs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "EpithetSearchDs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {searchText, new java.lang.Boolean(anywhereInText), new java.lang.Long(maxNumber)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponseEpithetSearchDsResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponseEpithetSearchDsResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponseEpithetSearchDsResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.NameByKeyResponseNameByKeyResult nameByKey(long nameKey) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/NameByKey");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameByKey"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Long(nameKey)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.NameByKeyResponseNameByKeyResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.NameByKeyResponseNameByKeyResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.NameByKeyResponseNameByKeyResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.NameByKeyDsResponseNameByKeyDsResult nameByKeyDs(long nameKey) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/NameByKeyDs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameByKeyDs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Long(nameKey)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.NameByKeyDsResponseNameByKeyDsResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.NameByKeyDsResponseNameByKeyDsResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.NameByKeyDsResponseNameByKeyDsResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String nameFullByKey(java.lang.String nameLsid) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/NameFullByKey");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameFullByKey"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {nameLsid});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.NameByKeyRDFResponseNameByKeyRDFResult nameByKeyRDF(java.lang.String nameLsid) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/NameByKeyRDF");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameByKeyRDF"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {nameLsid});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.NameByKeyRDFResponseNameByKeyRDFResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.NameByKeyRDFResponseNameByKeyRDFResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.NameByKeyRDFResponseNameByKeyRDFResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.AuthorSearchResponseAuthorSearchResult authorSearch(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/AuthorSearch");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AuthorSearch"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {searchText, new java.lang.Boolean(anywhereInText), new java.lang.Long(maxNumber)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.AuthorSearchResponseAuthorSearchResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.AuthorSearchResponseAuthorSearchResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.AuthorSearchResponseAuthorSearchResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponseAuthorSearchDsResult authorSearchDs(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/AuthorSearchDs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AuthorSearchDs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {searchText, new java.lang.Boolean(anywhereInText), new java.lang.Long(maxNumber)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponseAuthorSearchDsResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponseAuthorSearchDsResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponseAuthorSearchDsResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyResponseNamesByCurrentKeyResult namesByCurrentKey(long currentKey) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/NamesByCurrentKey");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NamesByCurrentKey"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Long(currentKey)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyResponseNamesByCurrentKeyResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyResponseNamesByCurrentKeyResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyResponseNamesByCurrentKeyResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponseNamesByCurrentKeyDsResult namesByCurrentKeyDs(long currentKey) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/NamesByCurrentKeyDs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NamesByCurrentKeyDs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Long(currentKey)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponseNamesByCurrentKeyDsResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponseNamesByCurrentKeyDsResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponseNamesByCurrentKeyDsResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponseAllUpdatedNamesResult allUpdatedNames(java.lang.String startDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/AllUpdatedNames");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AllUpdatedNames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {startDate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponseAllUpdatedNamesResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponseAllUpdatedNamesResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponseAllUpdatedNamesResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponseUpdatedNamesInRangeResult updatedNamesInRange(java.lang.String rank, java.lang.String startDate, java.lang.String endDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/UpdatedNamesInRange");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "UpdatedNamesInRange"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {rank, startDate, endDate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponseUpdatedNamesInRangeResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponseUpdatedNamesInRangeResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponseUpdatedNamesInRangeResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.UpdatedNamesResponseUpdatedNamesResult updatedNames(java.lang.String rank, java.lang.String startDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/UpdatedNames");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "UpdatedNames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {rank, startDate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.UpdatedNamesResponseUpdatedNamesResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.UpdatedNamesResponseUpdatedNamesResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.UpdatedNamesResponseUpdatedNamesResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponseNewNamesInRangeResult newNamesInRange(java.lang.String rank, java.lang.String startDate, java.lang.String endDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/NewNamesInRange");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NewNamesInRange"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {rank, startDate, endDate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponseNewNamesInRangeResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponseNewNamesInRangeResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponseNewNamesInRangeResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.NewNamesResponseNewNamesResult newNames(java.lang.String rank, java.lang.String startDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/NewNames");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NewNames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {rank, startDate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.NewNamesResponseNewNamesResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.NewNamesResponseNewNamesResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.NewNamesResponseNewNamesResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponseDeprecatedNamesResult deprecatedNames(java.lang.String startDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/DeprecatedNames");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "DeprecatedNames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {startDate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponseDeprecatedNamesResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponseDeprecatedNamesResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponseDeprecatedNamesResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.indexfungorum.cabi.fungusserver.DeprecatedNamesByRankResponseDeprecatedNamesByRankResult deprecatedNamesByRank(java.lang.String rank, java.lang.String startDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://Cabi/FungusServer/DeprecatedNamesByRank");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "DeprecatedNamesByRank"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {rank, startDate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.indexfungorum.cabi.fungusserver.DeprecatedNamesByRankResponseDeprecatedNamesByRankResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.indexfungorum.cabi.fungusserver.DeprecatedNamesByRankResponseDeprecatedNamesByRankResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.indexfungorum.cabi.fungusserver.DeprecatedNamesByRankResponseDeprecatedNamesByRankResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}

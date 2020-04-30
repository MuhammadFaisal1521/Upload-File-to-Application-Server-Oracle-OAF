package SRL.oracle.apps.fnd.ImportFromExcel.server;

import oracle.apps.fnd.framework.server.OAViewRowImpl;

import oracle.jbo.domain.Number;
import oracle.jbo.server.AttributeDefImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class DashBoardViewRowImpl extends OAViewRowImpl {
    public static final int TRANSACTIONID = 0;
    public static final int TRANSACTIONDATE = 1;
    public static final int SRLFNDIMPXSLSETUPID = 2;
    public static final int DESCRIPTION = 3;
    public static final int FILENAME = 4;
    public static final int REMARKS = 5;
    public static final int CREATIONDATE = 6;
    public static final int CREATEDBY = 7;
    public static final int USERNAME = 8;

    /**This is the default constructor (do not remove)
     */
    public DashBoardViewRowImpl() {
    }

    /**Gets the attribute value for the calculated attribute TransactionId
     */
    public Number getTransactionId() {
        return (Number) getAttributeInternal(TRANSACTIONID);
    }

    /**Sets <code>value</code> as the attribute value for the calculated attribute TransactionId
     */
    public void setTransactionId(Number value) {
        setAttributeInternal(TRANSACTIONID, value);
    }

    /**Gets the attribute value for the calculated attribute TransactionDate
     */
    public String getTransactionDate() {
        return (String) getAttributeInternal(TRANSACTIONDATE);
    }

    /**Sets <code>value</code> as the attribute value for the calculated attribute TransactionDate
     */
    public void setTransactionDate(String value) {
        setAttributeInternal(TRANSACTIONDATE, value);
    }

    /**Gets the attribute value for the calculated attribute SrlFndImpxslSetupId
     */
    public Number getSrlFndImpxslSetupId() {
        return (Number) getAttributeInternal(SRLFNDIMPXSLSETUPID);
    }

    /**Sets <code>value</code> as the attribute value for the calculated attribute SrlFndImpxslSetupId
     */
    public void setSrlFndImpxslSetupId(Number value) {
        setAttributeInternal(SRLFNDIMPXSLSETUPID, value);
    }

    /**Gets the attribute value for the calculated attribute Description
     */
    public String getDescription() {
        return (String) getAttributeInternal(DESCRIPTION);
    }

    /**Sets <code>value</code> as the attribute value for the calculated attribute Description
     */
    public void setDescription(String value) {
        setAttributeInternal(DESCRIPTION, value);
    }

    /**Gets the attribute value for the calculated attribute Filename
     */
    public String getFilename() {
        return (String) getAttributeInternal(FILENAME);
    }

    /**Sets <code>value</code> as the attribute value for the calculated attribute Filename
     */
    public void setFilename(String value) {
        setAttributeInternal(FILENAME, value);
    }

    /**Gets the attribute value for the calculated attribute Remarks
     */
    public String getRemarks() {
        return (String) getAttributeInternal(REMARKS);
    }

    /**Sets <code>value</code> as the attribute value for the calculated attribute Remarks
     */
    public void setRemarks(String value) {
        setAttributeInternal(REMARKS, value);
    }

    /**Gets the attribute value for the calculated attribute CreationDate
     */
    public String getCreationDate() {
        return (String) getAttributeInternal(CREATIONDATE);
    }

    /**Sets <code>value</code> as the attribute value for the calculated attribute CreationDate
     */
    public void setCreationDate(String value) {
        setAttributeInternal(CREATIONDATE, value);
    }

    /**Gets the attribute value for the calculated attribute CreatedBy
     */
    public String getCreatedBy() {
        return (String) getAttributeInternal(CREATEDBY);
    }

    /**Sets <code>value</code> as the attribute value for the calculated attribute CreatedBy
     */
    public void setCreatedBy(String value) {
        setAttributeInternal(CREATEDBY, value);
    }

    /**Gets the attribute value for the calculated attribute UserName
     */
    public String getUserName() {
        return (String) getAttributeInternal(USERNAME);
    }

    /**Sets <code>value</code> as the attribute value for the calculated attribute UserName
     */
    public void setUserName(String value) {
        setAttributeInternal(USERNAME, value);
    }

    /**getAttrInvokeAccessor: generated method. Do not modify.
     */
    protected Object getAttrInvokeAccessor(int index, 
                                           AttributeDefImpl attrDef) throws Exception {
        switch (index) {
        case TRANSACTIONID:
            return getTransactionId();
        case TRANSACTIONDATE:
            return getTransactionDate();
        case SRLFNDIMPXSLSETUPID:
            return getSrlFndImpxslSetupId();
        case DESCRIPTION:
            return getDescription();
        case FILENAME:
            return getFilename();
        case REMARKS:
            return getRemarks();
        case CREATIONDATE:
            return getCreationDate();
        case CREATEDBY:
            return getCreatedBy();
        case USERNAME:
            return getUserName();
        default:
            return super.getAttrInvokeAccessor(index, attrDef);
        }
    }

    /**setAttrInvokeAccessor: generated method. Do not modify.
     */
    protected void setAttrInvokeAccessor(int index, Object value, 
                                         AttributeDefImpl attrDef) throws Exception {
        switch (index) {
        default:
            super.setAttrInvokeAccessor(index, value, attrDef);
            return;
        }
    }
}

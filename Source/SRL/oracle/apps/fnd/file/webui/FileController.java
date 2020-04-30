/*===========================================================================+
 |   Copyright (c) 2001, 2005 Oracle Corporation, Redwood Shores, CA, USA    |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 +===========================================================================*/
package SRL.oracle.apps.fnd.file.webui;

import SRL.oracle.apps.fnd.file.server.FileSetupVOImpl;
import SRL.oracle.apps.fnd.file.server.FileSetupVORowImpl;
import SRL.oracle.apps.fnd.file.server.SrlFndAttachmentsViewImpl;
import SRL.oracle.apps.fnd.file.server.SrlFndAttachmentsViewRowImpl;
import SRL.oracle.apps.fnd.file.server.UploadFileAMImpl;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import oracle.apps.fnd.common.VersionInfo;
import oracle.apps.fnd.framework.OAException;
import oracle.apps.fnd.framework.server.OADBTransaction;
import oracle.apps.fnd.framework.webui.OAControllerImpl;
import oracle.apps.fnd.framework.webui.OAPageContext;
import oracle.apps.fnd.framework.webui.OAWebBeanConstants;
import oracle.apps.fnd.framework.webui.beans.OAWebBean;
import oracle.apps.fnd.framework.webui.beans.form.OAFormValueBean;
import oracle.apps.fnd.framework.webui.beans.layout.OAHeaderBean;

import oracle.apps.fnd.framework.webui.beans.message.OAMessageStyledTextBean;
import oracle.apps.fnd.framework.webui.beans.message.OAMessageTextInputBean;

import oracle.cabo.ui.data.DataObject;

import oracle.ias.cache.CacheAccess;

import oracle.jbo.RowSetIterator;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.domain.Number;

/**
 * Controller for ...
 */
public class FileController extends OAControllerImpl {
    public static final String RCS_ID = "$Header$";
    public static final boolean RCS_ID_RECORDED = 
        VersionInfo.recordClassVersion(RCS_ID, "%packagename%");

    /**
     * Layout and page setup logic for a region.
     * @param pageContext the current OA page context
     * @param webBean the web bean corresponding to the region
     */
    public void processRequest(OAPageContext pageContext, OAWebBean webBean) {
        super.processRequest(pageContext, webBean);

        UploadFileAMImpl am = (UploadFileAMImpl)pageContext.getApplicationModule(webBean);
        FileSetupVOImpl fileStepUpV = (FileSetupVOImpl) am.getFileSetupVO1();
        fileStepUpV.executeQuery();

    }

    /**
     * Procedure to handle form submissions for form elements in
     * a region.
     * @param pageContext the current OA page context
     * @param webBean the web bean corresponding to the region
     */
    public void processFormRequest(OAPageContext pageContext, 
                                   OAWebBean webBean) {
        ///Super object
        super.processFormRequest(pageContext, webBean);
        UploadFileAMImpl am = 
            (UploadFileAMImpl)pageContext.getApplicationModule(webBean);

        //Button & Partial Process request  Event 
        if (pageContext.getParameter(OAWebBeanConstants.EVENT_PARAM) != null) {
            String EventName = 
                new String(pageContext.getParameter(OAWebBeanConstants.EVENT_PARAM));
                
            // Coding for Event
            this.ActionEvent(pageContext, webBean, EventName, am);
        }

    }

    /// actionEvent handler

    private void ActionEvent(OAPageContext pageContext, OAWebBean webBean, 
                             String EventName, UploadFileAMImpl am) {

        if (EventName.equals("UploadFileToDB")) {
            this.UploadFileToDB(pageContext, webBean, am);
        }

        if (EventName.equals("Filename")) {
            this.downlaodFileFromServer(pageContext,webBean,am);
        }
        
        //SubGroupName
         if (EventName.equals("SubGroupName")) {
             this.QueryDetails(pageContext,webBean,am);
         }
        
    }

    ///Save Data

    public void Save(UploadFileAMImpl am) {
        am.getOADBTransaction().commit();
    }


    private void UploadFileToDB(OAPageContext pageContext, OAWebBean webBean, 
                                UploadFileAMImpl am)  {
        
        
        //Get Current Seected Record of Header.
        FileSetupVORowImpl  setupRec = (FileSetupVORowImpl) this.Get_SetupID(pageContext,webBean,am);
        
        if (setupRec==null)
        {
            throw new OAException("Please select any group from Header for upload files.",OAException.ERROR);
        }
        
        //AttachmentFile Data
        DataObject fileUploadData = 
            (DataObject)pageContext.getNamedDataObject("AddAttachmentFile");
        String fileName = 
            (String)fileUploadData.selectValue(null, "UPLOAD_FILE_NAME");
        if (fileName == null) {
            throw new OAException("Please select attachment file for  upload.", 
                                  OAException.ERROR);
        }
        
        
        // Document Informations / referenceNumber.GetValue
        OAMessageTextInputBean referenceNumber = (OAMessageTextInputBean) webBean.findChildRecursive("referenceNumber");
        String rf ;
        Number refNum=null;

        if (referenceNumber.getValue(pageContext)!=null)
            {
                
            try {
                rf=(String)referenceNumber.getValue(pageContext);
                refNum = new Number(rf);
            } 
            catch (NumberFormatException e) {
                // TODO
                throw new OAException("Please enter valid number in Reference field..",OAException.ERROR);
                }
                catch (SQLException e) {
                    // TODO
                    throw new OAException(e.getMessage(),OAException.ERROR);
                    }
            }
        
        
        SrlFndAttachmentsViewImpl AttachmentVo = 
            (SrlFndAttachmentsViewImpl)am.getSrlFndAttachmentsView1();
        AttachmentVo.executeQuery();
        SrlFndAttachmentsViewRowImpl attachmentRow = 
            (SrlFndAttachmentsViewRowImpl)AttachmentVo.createRow(); //Create new Record
        AttachmentVo.insertRow(attachmentRow);

        OADBTransaction DBT = 
            (OADBTransaction)am.getDBTransaction(); /// DB Transaction 
        Number Attachment_id = 
            new Number(DBT.getSequenceValue("SRL_FND_ATTACHMENT_SEQ")); //attachmentID
        
        attachmentRow.setAttachmentsId(Attachment_id);
        attachmentRow.setFilename(fileName);
        attachmentRow.setFormName(setupRec.getSrlFileSetupId());
        attachmentRow.setContentType((String)fileUploadData.selectValue(null, 
                                                                        "UPLOAD_FILE_MIME_TYPE"));
        
       if (refNum!=null)
       {
        attachmentRow.setTransactionId(refNum); //assign Transaction ID
        
       }

        this.uploadFileToServer(pageContext, fileName, 
                                createBlobDomain(fileUploadData),setupRec.getSrlFileServerPath());

        ///
        this.Save(am);

        //Set Details Where Clause
         AttachmentVo.setWhereClause("1=2");
        AttachmentVo.executeQuery();
        AttachmentVo.setWhereClause(null);
        AttachmentVo.setWhereClause("form_name="+setupRec.getSrlFileSetupId());
        AttachmentVo.executeQuery();

    }

    private BlobDomain createBlobDomain(DataObject pfileUploadData) {
        // init the internal variables
        InputStream in = null;
        BlobDomain blobDomain = null;
        OutputStream out = null;

        try {
            String pFileName = 
                (String)pfileUploadData.selectValue(null, "UPLOAD_FILE_NAME");
            BlobDomain uploadedByteStream = 
                (BlobDomain)pfileUploadData.selectValue(null, pFileName);
            // Get the input stream representing the data from the client
            in = uploadedByteStream.getInputStream();
            // create the BlobDomain datatype to store the data in the db
            blobDomain = new BlobDomain();
            // get the outputStream for hte BlobDomain
            out = blobDomain.getBinaryOutputStream();
            byte buffer[] = new byte[8192];
            for (int bytesRead = 0; 
                 (bytesRead = in.read(buffer, 0, 8192)) != -1; )
                out.write(buffer, 0, bytesRead);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        // return the filled BlobDomain
        return blobDomain;
    }

    public void uploadFileToServer(OAPageContext pageContext, String fileName, 
                                   BlobDomain pBlobDomain,String filePath) {
        try {
            FileOutputStream output = 
                new FileOutputStream(new File(filePath, fileName));
            for (int bytes = 0; bytes < pBlobDomain.getLength(); bytes++) {
                output.write(pBlobDomain.getInputStream().read());
            }
            output.close();
        } catch (Exception e) {
            throw new OAException("File Uploading error " + e.getMessage(), 
                                  OAException.ERROR);
        }

    }


    public void downlaodFileFromServer(OAPageContext pageContex,OAWebBean webBean,UploadFileAMImpl am) 
    {
        //Get Current Seected Record of Header.
        FileSetupVORowImpl  setupRec = (FileSetupVORowImpl) this.Get_SetupID(pageContex,webBean,am);
        HttpServletResponse response = 
            (HttpServletResponse)pageContex.getRenderingContext().getServletResponse();

        SrlFndAttachmentsViewRowImpl attachmnetROw =  (SrlFndAttachmentsViewRowImpl) am.findRowByRef(pageContex.getParameter(OAWebBeanConstants.EVENT_SOURCE_ROW_REFERENCE)) ; 
        String fileType = (String)attachmnetROw.getContentType();
        String fileName =  attachmnetROw.getFilename();
        String fileNameWithPath = setupRec.getSrlFileServerPath()+fileName;



        //Get File Name
        File fileToDownload = null;
        try {
            fileToDownload = new File(fileNameWithPath);
        } catch (Exception e) {
            throw new OAException("Invalid File Path or file does not exist.");
        }
        
        ///Read File
        if (!fileToDownload.canRead()) {
            throw new OAException("Not Able to read the file.");
        }
        
        response.setContentType(fileType);
        response.setContentLength((int)fileToDownload.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        InputStream in = null;
        ServletOutputStream outs = null;

        try {
            outs = response.getOutputStream();
            in = new BufferedInputStream(new FileInputStream(fileToDownload));
            int ch;
            while ((ch = in.read()) != -1) {
                outs.write(ch);
            }

        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        } finally {
            try {
                outs.flush();
                outs.close();
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /*Query Details Region*/
     private void QueryDetails(OAPageContext pageContext, OAWebBean webBean, UploadFileAMImpl am) 
    {
        
         //This Parameter is defined in Event parameters of Field  , Current Row SetupID
         String SrlFileSetupId = (String) pageContext.getParameter("SrlFileSetupId");
         //Set Setup ID in FormValue  --FormValue Name is SetupID.
         this.Set_SetupID(pageContext,webBean,SrlFileSetupId);


        //Get SetUpRecord 
        FileSetupVORowImpl setupRec = (FileSetupVORowImpl)this.Get_SetupID(pageContext,webBean,am);
        
        
        // Variable of View 
        SrlFndAttachmentsViewImpl attachmentVO = (SrlFndAttachmentsViewImpl) am.getSrlFndAttachmentsView1();

        //Set Details Where Clause
         attachmentVO.setWhereClause("1=2");
        attachmentVO.executeQuery();
        attachmentVO.setWhereClause(null);
        attachmentVO.setWhereClause("form_name="+setupRec.getSrlFileSetupId());
        attachmentVO.executeQuery();
        
        //Set Details Text
        OAMessageStyledTextBean TextB = (OAMessageStyledTextBean)webBean.findChildRecursive("DetailsText");
        TextB.setValue(pageContext,setupRec.getSrlFileSubGroupName()+"("+setupRec.getSrlFileSubGroupDescription()+")");
    }
    
    //Get SetUp Record
    public FileSetupVORowImpl Get_SetupID (OAPageContext pageContext, OAWebBean webBean, UploadFileAMImpl am) 
    {
        //QueryDetails method Store Value SetupID , Now we will Get it and use in below code.
        OAFormValueBean FV = (OAFormValueBean) webBean.findChildRecursive("SetupID");
        String l_FV = (String) FV.getValue(pageContext);
        
        if (l_FV==null)
        {
        throw new OAException("Please select any group from Header for upload files.",OAException.ERROR);
        }
        
        //Setup view Object 
        FileSetupVOImpl setupVO = (FileSetupVOImpl)am.getFileSetupVO1();
        
        //Iterator of Setup View  and set its Ranges 
        RowSetIterator  setupVO_Itr = this.GetRowIterator(setupVO);
        setupVO_Itr.setRangeStart(0);
        setupVO_Itr.setRangeStart(setupVO.getRowCount());
        
        //Setup view Record object
        FileSetupVORowImpl setupVO_rec = null;
        
        // Find the Record that is Select for Query Details Block and Upload Attachment
        //
        for(int i=0;i<setupVO.getRowCount();i++)
              {
                 /// Get Row 
                 // (0-Last Record) where Record=Parameter /Return record/ else /Null/
                  setupVO_rec =(FileSetupVORowImpl)setupVO_Itr.getRowAtRangeIndex(i);
                  
                  if (l_FV.equals(setupVO_rec.getSrlFileSetupId()))
                  {
                  return setupVO_rec;
                  }
              }
              
        return null;
    }
    
    
    //Set SetUpID
    public void Set_SetupID (OAPageContext pageContext, OAWebBean webBean, String SetupID) 
    {
        //Set SetupID Value 
        OAFormValueBean FV = (OAFormValueBean) webBean.findChildRecursive("SetupID");
        FV.setValue(pageContext,SetupID);
    }
    
    
    

      /*
       * GetRowIterator : Get RowSetIterator of View Objetc ,incase RowSetIterator is not assosiated then Create it.
       * @Param   ViewObjectIMP
       * */
      public RowSetIterator GetRowIterator(FileSetupVOImpl pViewObject)
      {
        if (pViewObject.getRowSetIterator() == null)
        {
          //Create New RowIterator 
          return pViewObject.createRowSetIterator("FileSetupVOImpl");
        }
        else
        {
          //Create Existing RowIterator 
          return pViewObject.getRowSetIterator();
        }
      }
    
}


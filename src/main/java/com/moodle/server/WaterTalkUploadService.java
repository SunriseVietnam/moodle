package com.moodle.server;

import com.google.appengine.api.datastore.Blob;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;

@SuppressWarnings("serial")
public class
WaterTalkUploadService extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        ServletFileUpload upload = new ServletFileUpload();

        try {
            FileItemIterator iter = upload.getItemIterator(request);

            while (iter.hasNext()) {
                FileItemStream item = iter.next();

                String name = item.getFieldName();
                InputStream stream = item.openStream();

                // Process the input stream
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int len;
                byte[] buffer = new byte[8192];
                while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, len);
                }

                //save file
                WaterTalkFiles file = new WaterTalkFiles(name, buffer);
                PersistenceManager pm = null;
                try {
                    pm = PMF.get().getPersistenceManager();
                    javax.jdo.Transaction transaction = pm.currentTransaction();
                    transaction.begin();
                    WaterTalkFiles guest = new WaterTalkFiles(name,buffer);
                    pm.makePersistent(guest);
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != pm)
                        pm.close();
                }


            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/plain");
        resp.setHeader("Content-Disposition", "attachment; filename=output.txt");
        PrintWriter out = null;
        try {
            out = resp.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.println("This is the output content");
        out.println("Probably something dynamic should go in here:::::");


        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
            javax.jdo.Transaction transaction = pm.currentTransaction();
            Extent e = pm.getExtent(WaterTalkFiles.class, true);
            Iterator iter = e.iterator();
            String returns = "";
            WaterTalkFiles file = (WaterTalkFiles) iter.next();
            Blob blob = file.getData();
            byte[] buffer = blob.getBytes();
            String s = new String(buffer);
            out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != pm)
                pm.close();
        }
    }
}


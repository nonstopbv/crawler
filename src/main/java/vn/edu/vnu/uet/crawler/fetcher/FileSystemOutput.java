/*
 * Copyright (C) 2014 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package vn.edu.vnu.uet.crawler.fetcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;




public class FileSystemOutput {

    public String root;

    public FileSystemOutput(String root) {
        this.root = root;
    }

    public void output(String url ,String text) {
        try {
            URL _URL = new URL(url);
            String query = "";
            if (_URL.getQuery() != null) {
                query = "_" + _URL.getQuery();
            }
            String path = _URL.getPath();
            if (path.length() == 0) {
                path = "index.html";
            } else {
                if (path.charAt(path.length() - 1) == '/') {
                    path = path + "index.html";
                } else {

                    for (int i = path.length() - 1; i >= 0; i--) {
                        if (path.charAt(i) == '/') {
                            if (!path.substring(i + 1).contains(".")) {
                                path = path + ".html";
                            }
                        }
                    }
                }
            }
            path += query;
            File domain_path = new File(root, _URL.getHost());
            File f = new File(domain_path, path);           
            writeFileWithParent(f, text);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteDir(File dir){
        File[] filelist=dir.listFiles();
        for(File file:filelist){
            if(file.isFile()){
                file.delete();
            }else{
                deleteDir(file);
            }
        }
        dir.delete();
    }
    
    public static void copy(File origin,File newfile) throws FileNotFoundException, IOException{
        if(!newfile.getParentFile().exists()){
            newfile.getParentFile().mkdirs();
        }
        FileInputStream fis=new FileInputStream(origin);
        FileOutputStream fos=new FileOutputStream(newfile);
        byte[] buf=new byte[2048];
        int read;
        while((read=fis.read(buf))!=-1){
            fos.write(buf,0,read);
        }
        fis.close();
        fos.close();
    }
    
    public static void writeFile(String filename,byte[] content) throws FileNotFoundException, IOException{
        FileOutputStream fos=new FileOutputStream(filename);
        fos.write(content);
        fos.close();
    }
    
    public static void writeFileWithParent(String filename,byte[] content) throws FileNotFoundException, IOException{
        File file=new File(filename);
        File parent=file.getParentFile();
        if(!parent.exists()){
            parent.mkdirs();
        }
        FileOutputStream fos=new FileOutputStream(file);
        fos.write(content);
        fos.close();
    }
    
    public static void writeFileWithParent(File file,String content) throws FileNotFoundException, IOException{
       
        File parent=file.getParentFile();
        if(!parent.exists()){
            parent.mkdirs();
        }
        FileOutputStream fos=new FileOutputStream(file);
        fos.write(content.getBytes(Charset.forName("UTF-8")));
        fos.close();
    }
    
    public static byte[] readFile(File file) throws IOException{
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[2048];
        int read;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((read = fis.read(buf)) != -1) {
            bos.write(buf, 0, read);
        }

        fis.close();
        return bos.toByteArray();
    }

}

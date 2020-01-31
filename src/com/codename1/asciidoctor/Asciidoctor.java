/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.asciidoctor;

import com.codename1.io.Util;
import com.codename1.processing.Result;
import com.codename1.ui.BrowserComponent;
import com.codename1.ui.CN;
import static com.codename1.ui.CN.callSerially;
import com.codename1.util.AsyncResource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class Asciidoctor {
    private BrowserComponent browserComponent;
    
    public static enum DocType {
        Article,
        Book,
        Manpage,
        Inline
    }

    
    public static class ConvertOptions {
        Map<String,String> attributes = new HashMap<>();
        String backend = "html5";
        String baseDir = null;
        boolean catalogAssets = false;
        DocType docType;
        boolean headerFooter;
        String safe;
        
        /**
         * Selects the converter to use (as registered with this keyword).
         * @param backend E.g. html5, docbook5, or any backend registered through an active converter
         * @return 
         */
        public ConvertOptions backend(String backend) {
            this.backend = backend;
            return this;
        }
        
        /**
         * Sets the base (aka working) directory containing the document and resources.
         * @param baseDir The directory of the source file, or the working directory if the source is read from a stream.
         * @return 
         */
        public ConvertOptions baseDir(String baseDir) {
            this.baseDir = baseDir;
            return this;
        }
        
        /**
         * If true, tells the parser to capture images and links in the reference table. (Normally only IDs, footnotes and indexterms are included). The reference table is available via the references property on the document AST object.
         * @param catalogAssets
         * @return 
         */
        public ConvertOptions catalogAssets(boolean catalogAssets) {
            this.catalogAssets = catalogAssets;
            return this;
        }
        
        /**
         * Sets the document type.
         * @param docType Default is Article
         * @return 
         */
        public ConvertOptions docType(DocType docType) {
            this.docType = docType;
            return this;
        }
        
        /**
         * If true, add the document header and footer (i.e., framing) around the body content in the output.
         * @param headerFooter
         * @return 
         */
        public ConvertOptions headerFooter(boolean headerFooter) {
            this.headerFooter = headerFooter;
            return this;
        }
        
        /**
         * Sets the safe mode.
         * @param safe unsafe, safe, server or secure
         * @return 
         */
        public ConvertOptions safe(String safe) {
            this.safe = safe;
            return this;
        }
        
        /**
         * Sets additional document attributes, which override equivalently-named attributes defined in the document unless the value ends with @.
         * @param key
         * @param value
         * @return 
         */
        public ConvertOptions attr(String key, String value) {
            attributes.put(key, value);
            return this;
        }
        
        private String toJSON() {
            Map m = new HashMap();
            if (backend != null) {
                m.put("backend", backend);
            }
            if (baseDir != null) {
                m.put("base_dir", baseDir);
            }
            m.put("catalog_assets", catalogAssets);
            
            if (docType != null) {
                m.put("doctype", docType.toString().toLowerCase());
            }
            
            m.put("header_footer", headerFooter);
            if (safe != null) {
                m.put("safe", safe);
            }
            if (!attributes.isEmpty()) {
                m.put("attributes", attributes);
            }
            return Result.fromContent(m).toString();
        }
    }
    
    private void init() throws IOException {
        if (browserComponent == null) {
            browserComponent = new BrowserComponent();
            StringBuilder html = new StringBuilder();
            html.append("<!doctype html>\n")
                    .append("<html><head><script>\n")
                    .append(Util.readToString(CN.getResourceAsStream("/com.codename1.asciidoctor.js")))
                    .append("\n</script></head><body></body></html>");
            browserComponent.setPage(html.toString(), null);
            
        }
    }
    
    /**
     * The result of an Asciidoc conversion.  Retrieve the HTML string result
     * using the ready() callback.
     */
    public class HtmlConversion extends AsyncResource<String> {
        
    }
    
    /**
     * Gets the CSS stylesheet contents that can be embedded inside the style tag of HTML.
     * @return
     * @throws IOException 
     */
    public String getCSS() throws IOException {
        return Util.readToString(CN.getResourceAsStream("/com.codename1.asciidoctor.css"));
    }
    
    /**
     * Converts Asciidoc text to HTML.
     * @param asciidocText Text formatted in Asciidoc format
     * @return An AsyncResource that can be queried for the result.
     */
    public HtmlConversion toHtml(String asciidocText, ConvertOptions opts)  {
        
        HtmlConversion out = new HtmlConversion();
        if (!CN.isEdt()) {
            callSerially(()->toHtml(asciidocText, opts));
            return out;
        }
        try {
            init();
            
            browserComponent.execute("try {var asciidoctor = new Asciidoctor(); var html = asciidoctor.convert(${0}, "+opts.toJSON()+"); callback.onSuccess(html)} catch (e) {callback.onError(''+e)}", 
                    new Object[]{asciidocText}, 
                    res->{
                out.complete(res.getValue());
            });
            
        } catch (IOException ex) {
            out.error(ex);
        }
        return out;
    }
}

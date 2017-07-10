package com.instrasoft.csp.ccs.controller;


import com.instrasoft.csp.ccs.config.DataContextUrl;
import com.instrasoft.csp.ccs.config.HttpStatusResponseType;
import com.instrasoft.csp.ccs.config.PagesContextUrl;
import com.instrasoft.csp.ccs.domain.api.Response;
import com.instrasoft.csp.ccs.domain.api.ResponseError;
import com.instrasoft.csp.ccs.domain.data.DashboardRow;
import com.instrasoft.csp.ccs.domain.data.ModuleRow;
import com.instrasoft.csp.ccs.domain.postgresql.Csp;
import com.instrasoft.csp.ccs.domain.postgresql.Module;
import com.instrasoft.csp.ccs.domain.postgresql.ModuleVersion;
import com.instrasoft.csp.ccs.repository.CspRepository;
import com.instrasoft.csp.ccs.repository.ModuleRepository;
import com.instrasoft.csp.ccs.repository.ModuleVersionRepository;
import com.instrasoft.csp.ccs.utils.JodaConverter;
import com.instrasoft.csp.ccs.utils.VersionParser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DataController implements DataContextUrl, PagesContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(DataController.class);

    @Value("${server.file.repository}")
    String fileRepository;
    @Value("${server.file.temp}")
    String fileTemp;
    @Value("${server.digest.algorithm}")
    String digestAlgorithm;

    @Autowired
    CspRepository cspRepository;

    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    ModuleVersionRepository moduleVersionRepository;


    @RequestMapping(value = DATA_BASEURL + DATA_DASHBOARD,
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity dashboard() {
        List<DashboardRow> rows = new ArrayList<>();
        List<Csp> csps = cspRepository.findAll();


        for (Csp csp : csps) {
            DashboardRow row = new DashboardRow();

            row.setIcon("<i class=\"fa fa-cog\"></i>");
            row.setName(csp.getName());
            row.setDomain(csp.getDomainName());
            row.setTs("<span class=\"text-success\">" + csp.getRegistrationDate() + "</span>");
            /**
             * @TODO
             */
            row.setStatus("<span class=\"text-success\"><i class=\"fa fa-info-circle\"></i> Up-to-date</span>");
            List<String> confUpdates = new ArrayList<>();
            List<String> reportUpdates = new ArrayList<>();
            row.setConfUpdates(confUpdates);
            row.setReportUpdates(reportUpdates);
            row.setBtn("<a class=\"btn btn-xs btn-default\" href=\""+PAGES_MANAGE+"?cspId=" + csp.getId() + "\"><i class=\"fa fa-wrench\"></i> Manage</a>");

            rows.add(row);
        }

        return new ResponseEntity<>(rows, HttpStatus.OK);
    }


    @RequestMapping(value = DATA_BASEURL + DATA_MODULES,
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity modules() {
        List<ModuleRow> rows = new ArrayList<>();
        List<Module> modules = moduleRepository.findAll();

        for (Module module : modules) {
            ModuleRow row = new ModuleRow();
            row.setIcon("<i class=\"fa fa-circle-o-notch\"></i>");
            row.setShortName(module.getName());

            ModuleVersion moduleVersion = moduleVersionRepository.findByModuleId(module.getId());
            row.setFullName(moduleVersion.getFullName());
            row.setVersion(VersionParser.toString(moduleVersion.getVersion()));
            row.setReleased(moduleVersion.getReleasedOn());

            row.setIsDefault("<i class=\"fa fa-square-o\"></i>");
            if (module.getIsDefault() == 1) {
                row.setIsDefault("<i class=\"fa fa-square text-success\"></i>");
            }

            row.setPriority(module.getStartPriority());
            row.setHash(moduleVersion.getHash());
            row.setBtn("<a class=\"btn btn-xs btn-success\" href=\""+PAGES_MODULE_UPDATE+"?moduleId=" + module.getId()+"\"><i class=\"fa fa-edit\"></i></a>"+
                    "&nbsp;"+
                    "<a class=\"btn btn-xs btn-default\" href=\"#\"><i class=\"fa fa-remove\"></i></a>");

            rows.add(row);
        }

        return new ResponseEntity<>(rows, HttpStatus.OK);
    }

    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_SAVE,
            headers=("content-type=multipart/*"),
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity addModule(@RequestParam("module_short_name") String shortName,
                                  @RequestParam("module_full_name") String fullName,
                                  @RequestParam("module_version") String version,
                                  @RequestParam(value = "module_default", required = false, defaultValue = "") String isDefault,
                                  @RequestParam("module_priority") Integer priority,
                                  @RequestParam("module_file") MultipartFile uploadFile) {

        if (uploadFile.isEmpty()) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_SAVE_EMPTY_FILE.code(), HttpStatusResponseType.DATA_MODULE_SAVE_EMPTY_FILE.text(), HttpStatusResponseType.DATA_MODULE_SAVE_EMPTY_FILE.exception());
            return new ResponseEntity<>(error, HttpStatus.OK);
        }

        try {
            Module module = new Module();
            module.setName(shortName);
            module.setStartPriority(priority);
            module.setIsDefault(0);
            if (isDefault.equals("on")) {
                module.setIsDefault(1);
            }
            String hash = this.saveUploadedFile(uploadFile);

            //save after all exceptions
            module = moduleRepository.save(module);
            LOG.info(module.getId().toString());
            ModuleVersion moduleVersion = new ModuleVersion();
            moduleVersion.setModuleId(module.getId());
            moduleVersion.setFullName(fullName);
            moduleVersion.setVersion(VersionParser.fromString(version));
            moduleVersion.setReleasedOn(JodaConverter.getCurrentJodaString());
            moduleVersion.setHash(hash);
            moduleVersionRepository.save(moduleVersion);

            /**
             * @TODO Check if {@link ModuleVersion exists}????
             */
            Response response = new Response(HttpStatusResponseType.DATA_MODULE_SAVE_OK.code(), HttpStatusResponseType.DATA_MODULE_SAVE_OK.text());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            if (e instanceof NoSuchAlgorithmException || e instanceof IOException || e instanceof NoSuchFileException) {
                ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_SAVE_HASH_FILE.code(), HttpStatusResponseType.DATA_MODULE_SAVE_HASH_FILE.text(), HttpStatusResponseType.DATA_MODULE_SAVE_HASH_FILE.exception());
                return new ResponseEntity<>(error, HttpStatus.OK);
            }
            else {
                ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_SAVE_UNIQUE.code(), HttpStatusResponseType.DATA_MODULE_SAVE_UNIQUE.text(), HttpStatusResponseType.DATA_MODULE_SAVE_UNIQUE.exception());
                return new ResponseEntity<>(error, HttpStatus.OK);
            }
        }
    }

    private String saveUploadedFile(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        /*
        Save file to temp
         */
        byte[] bytes = file.getBytes();
        File f = new File(fileTemp + file.getOriginalFilename());
        Path path = Paths.get(f.getAbsolutePath());
        Files.write(path, bytes);

        /*
        Calculate hash
         */
        String hash = this.hashFile(f.getAbsolutePath());

        /*
        Clean up files
         */
        /**
         * @TODO What is file has the same hash? Can it happen?? Hash Unique
         */
        //overwrite existing file, if exists
        CopyOption[] options = new CopyOption[]{
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
        };
        File target = new File(fileRepository + hash + "." + FilenameUtils.getExtension(f.getAbsolutePath()));
        Path FROM = Paths.get(f.getAbsolutePath());
        Path TO = Paths.get(target.getAbsolutePath());
        Files.copy(FROM, TO, options);
        Files.delete(FROM);

        return hash;
    }

    private String hashFile(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
        FileInputStream fis = new FileInputStream(filePath);

        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        };
        byte[] mdbytes = md.digest();
        fis.close();

        //convert the byte to hex format
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<mdbytes.length;i++) {
            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
        }

        return hexString.toString();
    }
}

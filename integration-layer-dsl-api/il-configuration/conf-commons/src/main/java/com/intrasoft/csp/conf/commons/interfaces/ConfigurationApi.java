package com.intrasoft.csp.conf.commons.interfaces;


import com.intrasoft.csp.conf.commons.exceptions.InvalidCspEntryException;
import com.intrasoft.csp.conf.commons.model.AppInfoDTO;
import com.intrasoft.csp.conf.commons.model.RegistrationDTO;
import com.intrasoft.csp.conf.commons.model.ResponseDTO;
import com.intrasoft.csp.conf.commons.model.UpdateInformationDTO;
import org.springframework.http.ResponseEntity;


public interface ConfigurationApi {

    /**
     * Retrieves a list of available updates, for registered modules of the CSP.
     * @param cspId A unique identifier that defines a Registered and Known CSP. The csp identifier follows the UUID
     *              formatted as text, for 36 characters total, arranged as 8-4-4-4-12.
     * @return UpdateInformationDTO
     */
    UpdateInformationDTO updates(String cspId) throws InvalidCspEntryException;

    /**
     * Register a NEW csp or register for an existing CSP the modules that are being installed
     * @param cspId A unique identifier that defines a Registered and Known CSP. The csp identifier follows the UUID
     *              formatted as text, for 36 characters total, arranged as 8-4-4-4-12.
     * @param cspRegistration A block of information to register the CSP being installed
     */
    ResponseDTO register(String cspId, RegistrationDTO cspRegistration);

    /**
     * Retrieves a list of available updates, for registered modules of the CSP.
     * @param cspId A unique identifier that defines a Registered and Known CSP. The csp identifier follows the UUID
     *              formatted as text, for 36 characters total, arranged as 8-4-4-4-12.
     * @param updateHash A unique identifier hash for the given update. The system must verify that this hash is
     *                   available for this cspId to download; then it provides the byte stream for this update object.
     * @return ResponseEntity
     */
    ResponseEntity update(String cspId, String updateHash);

    /**
     * Submits a body that contains information of the CSP
     * @param cspId A unique identifier that defines a Registered and Known CSP. The csp identifier follows the UUID
     *              formatted as text, for 36 characters total, arranged as 8-4-4-4-12.
     */
    void appInfo(String cspId, AppInfoDTO appInfo);
}

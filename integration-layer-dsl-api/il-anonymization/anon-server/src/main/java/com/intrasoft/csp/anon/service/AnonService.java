package com.intrasoft.csp.anon.service;

import com.intrasoft.csp.anon.commons.interfaces.AnonymizationApi;
import com.intrasoft.csp.anon.commons.model.MappingDTO;
import com.intrasoft.csp.anon.commons.model.SaveMappingDTO;

public interface AnonService extends AnonymizationApi{
    MappingDTO saveMapping(SaveMappingDTO saveMappingDTO);
}

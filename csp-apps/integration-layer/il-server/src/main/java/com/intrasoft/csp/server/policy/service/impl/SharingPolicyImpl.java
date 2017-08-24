package com.intrasoft.csp.server.policy.service.impl;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.server.policy.domain.entity.Policy;
import com.intrasoft.csp.server.policy.domain.exception.CouldNotDeleteException;
import com.intrasoft.csp.server.policy.domain.exception.PolicyNotFoundException;
import com.intrasoft.csp.server.policy.domain.exception.PolicySaveException;
import com.intrasoft.csp.server.policy.domain.model.EvaluatedPolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.PolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.SharingPolicyAction;
import com.intrasoft.csp.server.policy.domain.repository.PolicyRepository;
import com.intrasoft.csp.server.policy.service.SharingPolicyService;
import com.intrasoft.csp.server.utils.Conversions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class SharingPolicyImpl implements SharingPolicyService, Conversions{
    private static final Logger LOG = LoggerFactory.getLogger(SharingPolicyImpl.class);

    @Autowired
    PolicyRepository policyRepository;

    @Override
    public List<PolicyDTO> getPolicies() {
        List<Policy> list = policyRepository.findAll();
        return list.stream().map(p->convertPolicyToDTO.apply(p)).collect(Collectors.toList());
    }

    @Override
    public List<PolicyDTO> getPoliciesByAction(SharingPolicyAction sharingPolicyAction) {
        List<Policy> list = policyRepository.findBySharingPolicyAction(sharingPolicyAction);
        return list.stream().map(p->convertPolicyToDTO.apply(p)).collect(Collectors.toList());
    }

    @Override
    public List<PolicyDTO> getPoliciesByDataType(IntegrationDataType integrationDataType) {
        List<Policy> list = policyRepository.findByIntegrationDataType(integrationDataType);
        return list.stream().map(p->convertPolicyToDTO.apply(p)).collect(Collectors.toList());
    }

    @Override
    public EvaluatedPolicyDTO evaluate(IntegrationDataType integrationDataType) {
        List<Policy> list = policyRepository.findByIntegrationDataType(integrationDataType);
        Optional<Policy> appliedPolicy = list.stream()
                .filter(p->p.getActive()!=null && p.getActive())
                .max(Comparator.comparing(p->p.getSharingPolicyAction().priority()));//get the action with the highest priority
        if(!appliedPolicy.isPresent()) {
            return new EvaluatedPolicyDTO(SharingPolicyAction.NO_ACTION_FOUND, null);
        }else{
            return new EvaluatedPolicyDTO(appliedPolicy.get().getSharingPolicyAction(),convertPolicyToDTO.apply(appliedPolicy.get()));
        }
    }

    @Override
    public PolicyDTO savePolicy(PolicyDTO policyDTO) {
        Policy policy;
        if(policyDTO.getId() != null){
            // UPDATE
            policy = policyRepository.findOne(policyDTO.getId());
        }else{
            // INSERT
            policy = new Policy();
            if(StringUtils.isEmpty(policyDTO.getCondition()) || policyDTO.getIntegrationDataType() == null
                    || policyDTO.getSharingPolicyAction() == null){
                throw new PolicySaveException("The sharing policy object you provided could not be saved. " +
                        "Check if passing empty or null fields");
            }
        }

        policy.setActive(policyDTO.getActive());

        if(!StringUtils.isEmpty(policyDTO.getCondition())){
            policy.setPolicyCondition(policyDTO.getCondition());
        }

        if(policyDTO.getIntegrationDataType() != null ){
            policy.setIntegrationDataType(policyDTO.getIntegrationDataType());
        }

        if(policyDTO.getSharingPolicyAction() != null ){
            policy.setSharingPolicyAction(policyDTO.getSharingPolicyAction());
        }

        Policy savedPolicy = policyRepository.save(policy);
        return convertPolicyToDTO.apply(savedPolicy);
    }

    @Override
    public void deletePolicy(Integer id) {
        try {
            policyRepository.delete(id);
        }catch (EmptyResultDataAccessException e){
            throw new CouldNotDeleteException(String.format("Could not delete policy with this id: %d. Does it exist?",id));
        }
    }

    @Override
    public void deleteAllPolicies() {
        policyRepository.deleteAll();
    }

    @Override
    public PolicyDTO getPolicyById(Integer id) {
        Policy policy = policyRepository.findOne(id);
        if(policy == null){
            throw new PolicyNotFoundException("Could not find a policy with this id: "+id);
        }
        return convertPolicyToDTO.apply(policy);
    }

    @Override
    public Boolean checkCondition(String condition, IntegrationData integrationData, Team team) throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        @SuppressWarnings("unchecked")
        BiFunction<Object, Object, Object> biF = ( BiFunction<Object, Object, Object>)engine.eval(
                String.format("new java.util.function.BiFunction(%s)", condition));
        Boolean ret = (Boolean) biF.apply(integrationData,team);
        return ret;
    }
}

package com.intrasoft.csp.server.policy.controller;

import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.libraries.headersauth.User;
import com.intrasoft.csp.server.policy.domain.SharingPolicyRoutes;
import com.intrasoft.csp.server.policy.domain.model.PolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.SharingPolicyAction;
import com.intrasoft.csp.server.policy.service.SharingPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SharingPolicyController implements SharingPolicyRoutes {

    @Autowired
    SharingPolicyService sharingPolicyService;

    @ModelAttribute("integrationDataTypes")
    public IntegrationDataType[] integrationDataTypes() {
        return IntegrationDataType.values();
    }

    @ModelAttribute("sharingPolicyActions")
    public SharingPolicyAction[] sharingPolicyActions() {
        return SharingPolicyAction.values();
    }

    @ModelAttribute("policies")
    public List<PolicyDTO> getPolicies() {
        return sharingPolicyService.getPolicies();
    }

    @ModelAttribute("user")
    public User getUser(@AuthenticationPrincipal final User user){
        return user;
    }

    @RequestMapping("/")
    public String home(final Model model,@ModelAttribute("policy") PolicyDTO policy) {
        return "redirect:"+BASE_URL;
    }

    @RequestMapping(BASE_URL)
    public String viewPolicy(final Model model,@ModelAttribute("policy") PolicyDTO policy) {
        return HOME_TH;
    }

    @RequestMapping(BASE_URL+"/{id}")
    public String getPolicy(@PathVariable Integer id, final Model model) {
        PolicyDTO policyDTO = sharingPolicyService.getPolicyById(id);
        model.addAttribute("policy",policyDTO);
        return HOME_TH;
    }

    @RequestMapping(value = SAVE_URL,method = RequestMethod.POST)
    public String save(final Model model, RedirectAttributes redirect,
                       @ModelAttribute("policy") PolicyDTO policyDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return HOME_TH;
        }
        sharingPolicyService.savePolicy(policyDTO);
        redirect.addFlashAttribute("msg", "Policy saved");
        return "redirect:"+BASE_URL;
    }

    @RequestMapping(DELETE_URL+"/{id}")
    public String delete(@PathVariable Integer id, final Model model,RedirectAttributes redirect) {
        sharingPolicyService.deletePolicy(id);
        redirect.addFlashAttribute("msg", "Policy deleted");
        return "redirect:"+BASE_URL;
    }
}

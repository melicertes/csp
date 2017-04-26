package eu.europa.csp.vcbadmin.config;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.ITemplateResolutionValidity;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.NonCacheableTemplateResolutionValidity;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.Validate;

/**
 * This non-standard "template resolver" always return the template contents provided in the constructor.
 */
class MemoryTemplateResolver implements ITemplateResolver {

    private static final String NAME = "MemoryTemplateResolver";
    private static final Integer ORDER = 1;
    
    private final String templateContent;
    private final String templateMode;

    public MemoryTemplateResolver(final String templateContent, final String templateMode) {
        Validate.notNull(templateContent, "Template content must be non-null");
        Validate.notNull(templateMode, "Template mode must be non-null");
        this.templateContent = templateContent;
        this.templateMode = templateMode;
    }

    @Override
    public void initialize() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Integer getOrder() {
        return ORDER;
    }
    
    @Override
    public TemplateResolution resolveTemplate(final TemplateProcessingParameters tpp) {
        String templateName = "CustomTemplate";
        String resourceName = "CustomResource";
        IResourceResolver resourceResolver = new FixedMemoryResourceResolver(templateContent);
        String characterEncoding = "utf-8";
        ITemplateResolutionValidity validity = new NonCacheableTemplateResolutionValidity();
        return new TemplateResolution(templateName, resourceName, resourceResolver, characterEncoding, templateMode, validity);
    }
}
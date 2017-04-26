package eu.europa.csp.vcbadmin.config;

import org.thymeleaf.context.IContext;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.util.Validate;

public class StaticTemplateExecutor {
    
    private static final String TEMPLATE_NAME = "custom";

    private final String templateMode;
    
    private final IContext context;
    
    private final IMessageResolver messageResolver;
    
    public StaticTemplateExecutor(final IContext context, final IMessageResolver messageResolver, final String templateMode) {
        Validate.notNull(context, "Context must be non-null");
        Validate.notNull(templateMode, "Template mode must be non-null");
        Validate.notNull(messageResolver, "MessageResolver must be non-null");
        this.context = context;
        this.templateMode = templateMode;
        this.messageResolver = messageResolver;
    }
    
    public String processTemplateCode(final String code) {
        Validate.notNull(code, "Code must be non-null");
        ITemplateResolver templateResolver = new MemoryTemplateResolver(code, templateMode);
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setMessageResolver(messageResolver);
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.initialize();
        return templateEngine.process(TEMPLATE_NAME, context);
    }    
}
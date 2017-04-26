package eu.europa.csp.vcbadmin.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.util.Validate;

class FixedMemoryResourceResolver implements IResourceResolver {

	private static final String NAME = "FixedMemoryResourceResolver";

	private final String templateContent;

	public FixedMemoryResourceResolver(final String templateContent) {
		Validate.notNull(templateContent, "Template content must be non-null");
		this.templateContent = templateContent;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public InputStream getResourceAsStream(final TemplateProcessingParameters tpp, final String templateName) {
		return new ByteArrayInputStream(templateContent.getBytes());
	}
}

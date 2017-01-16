/*
 * Copyright (c) Interactive Information R & D (I2RD) LLC.
 * All Rights Reserved.
 *
 * This software is confidential and proprietary information of
 * I2RD LLC ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered
 * into with I2RD.
 */

package net.venturetech.sshd_test.profile.ui.company;

import net.venturetech.sshd_test.profile.model.company.Company;
import net.venturetech.sshd_test.profile.model.location.Location;
import net.venturetech.sshd_test.profile.service.SelectedCompanyTermProvider;
import net.venturetech.sshd_test.support.service.AppUtil;
import net.venturetech.sshd_test.support.ui.CommonEditorFields;
import net.venturetech.sshd_test.support.ui.UIHelper;
import net.venturetech.sshd_test.support.ui.contact.AddressValueEditor;
import net.venturetech.sshd_test.support.ui.contact.AddressValueEditor.AddressValueEditorConfig;
import net.venturetech.sshd_test.support.ui.contact.PhoneNumberValueEditor;
import net.venturetech.sshd_test.support.ui.contact.PhoneNumberValueEditor.PhoneNumberValueEditorConfig;
import net.venturetech.sshd_test.support.ui.vtcrop.VTCropPictureEditor;
import net.venturetech.sshd_test.support.ui.vtcrop.VTCropPictureEditorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;

import net.proteusframework.core.StringFactory;
import net.proteusframework.core.html.HTMLElement;
import net.proteusframework.core.locale.annotation.I18N;
import net.proteusframework.core.locale.annotation.I18NFile;
import net.proteusframework.core.locale.annotation.L10N;
import net.proteusframework.core.notification.Notifiable;
import net.proteusframework.data.filesystem.http.FileEntityFileItem;
import net.proteusframework.ui.miwt.component.Container;
import net.proteusframework.ui.miwt.component.Field;
import net.proteusframework.ui.miwt.component.Label;
import net.proteusframework.ui.miwt.component.composite.editor.CompositeValueEditor;
import net.proteusframework.ui.miwt.component.composite.editor.TextEditor;
import net.proteusframework.ui.miwt.component.composite.editor.URLEditor;

import static net.venturetech.sshd_test.profile.ui.UIText.INSTRUCTIONS_PICTURE_EDITOR_FMT;
import static net.venturetech.sshd_test.profile.ui.company.CompanyValueEditorLOK.*;
import static com.i2rd.miwt.util.CSSUtil.CSS_INSTRUCTIONS;
import static net.proteusframework.core.StringFactory.stringToURL;
import static net.proteusframework.core.StringFactory.urlToString;
import static net.proteusframework.core.html.HTMLElement.span;
import static net.proteusframework.core.locale.TextSources.createText;

/**
 * {@link CompositeValueEditor} for {@link Company}
 *
 * @author Alan Holt (aholt@venturetech.net)
 * @since 6 /27/16 1:49 PM
 */
@I18NFile(
    symbolPrefix = "net.venturetech.sshd_test.profile.ui.company.CompanyValueEditor",
    classVisibility = I18NFile.Visibility.PUBLIC,
    i18n = {
        @I18N(symbol = "Label LinkedIn", l10n = @L10N("LinkedIn")),
        @I18N(symbol = "Label Twitter", l10n = @L10N("Twitter")),
        @I18N(symbol = "Label Facebook", l10n = @L10N("Facebook")),
        @I18N(symbol = "Label Web Logo", l10n = @L10N("Web Logo")),
        @I18N(symbol = "Label Email Logo", l10n = @L10N("Email Logo")),
        @I18N(symbol = "Label Website", l10n = @L10N("Website")),
        @I18N(symbol = "Label Sub-Domain",
            l10n = @L10N("Sub-Domain")),
        @I18N(symbol = "Instructions Sub-Domain",
            description = "Params: {0:ProfileTermProvider.company}",
            l10n = @L10N("Enter a subdomain for this {0}.  "
                         + "The subdomain entered will automatically be converted to a valid format."
                         + "If a custom domain name is needed, please contact Support.")),
        @I18N(symbol = "Instructions Custom Domain",
            description = "Params: {0:ProfileTermProvider.company}",
            l10n = @L10N("This {0} is using a custom domain.  If you would like to change this, please contact Support."))
    }
)
@Configurable
public class CompanyValueEditor extends CompositeValueEditor<Company>
{
    private static final Pattern HOSTNAME_VALIDITY_PATTERN1 = Pattern.compile("_| ");
    private static final Pattern HOSTNAME_VALIDITY_PATTERN2 = Pattern.compile("[^a-zA-Z\\-0-9]");
    @Autowired private CompanyConfig _companyConfig;
    @Autowired private AppUtil _appUtil;
    @Autowired private SelectedCompanyTermProvider _terms;
    @Autowired private UIHelper _uiHelper;

    private VTCropPictureEditor _webLogoEditor;
    private VTCropPictureEditor _emailLogoEditor;
    private AbstractCompanyPropertyEditor.EditMode _editMode = AbstractCompanyPropertyEditor.EditMode.StandardCompany;

    /**
     * Instantiates a new company value editor.
     */
    public CompanyValueEditor()
    {
        super(Company.class);
    }

    /**
     * Sets edit mode.
     *
     * @param editMode the edit mode
     */
    public void setEditMode(AbstractCompanyPropertyEditor.EditMode editMode)
    {
        _editMode = editMode;
    }

    /**
     * With edit mode company value editor.
     *
     * @param editMode the edit mode
     *
     * @return the company value editor
     */
    public CompanyValueEditor withEditMode(AbstractCompanyPropertyEditor.EditMode editMode)
    {
        setEditMode(editMode);
        return this;
    }

    @Override
    public void init()
    {
        VTCropPictureEditorConfig webLogoConfig = _companyConfig.companyWebLogoConfig();
        _webLogoEditor = new VTCropPictureEditor(webLogoConfig);
        _webLogoEditor.addClassName("company-web-logo");
        _webLogoEditor.setDefaultResource(_appUtil.getDefaultResourceImage());

        VTCropPictureEditorConfig emailLogoConfig = _companyConfig.companyEmailLogoConfig();
        _emailLogoEditor = new VTCropPictureEditor(emailLogoConfig);
        _emailLogoEditor.addClassName("company-web-logo");
        _emailLogoEditor.setDefaultResource(_appUtil.getDefaultResourceImage());

        super.init();

        Label webLogoInstructions = new Label(createText(INSTRUCTIONS_PICTURE_EDITOR_FMT(),
            webLogoConfig.getCropWidth(),
            webLogoConfig.getCropHeight()));
        webLogoInstructions.addClassName(CSS_INSTRUCTIONS);
        webLogoInstructions.withHTMLElement(HTMLElement.div);

        Label emailLogoInstructions = new Label(createText(INSTRUCTIONS_PICTURE_EDITOR_FMT(),
            emailLogoConfig.getCropWidth(),
            emailLogoConfig.getCropHeight()));
        emailLogoInstructions.addClassName(CSS_INSTRUCTIONS);
        emailLogoInstructions.withHTMLElement(HTMLElement.div);

        add(of("logos",
            of("prop", LABEL_WEB_LOGO(), _webLogoEditor, webLogoInstructions),
            of("prop", LABEL_EMAIL_LOGO(), _emailLogoEditor, emailLogoInstructions)));
        CommonEditorFields.addNameEditor(this);
        addEditorForProperty(() -> {
            final CompositeValueEditor<Location> editor = new CompositeValueEditor<>(Location.class);

            editor.addEditorForProperty(() -> {
                AddressValueEditorConfig cfg = new AddressValueEditorConfig();
                return new AddressValueEditor(cfg);
            }, Location.ADDRESS_PROP);

//            editor.addEditorForProperty(() -> {
//                EmailAddressValueEditorConfig cfg = new EmailAddressValueEditorConfig();
//                return new EmailAddressValueEditor(cfg);
//            }, Location.EMAIL_ADDRESS_PROP);

            editor.addEditorForProperty(() -> {
                PhoneNumberValueEditorConfig cfg = new PhoneNumberValueEditorConfig();
                return new PhoneNumberValueEditor(cfg);
            }, Location.PHONE_NUMBER_PROP);

            return editor;
        }, Company.PRIMARY_LOCATION_PROP);

        addEditorForProperty(() -> {
            final URLEditor editor = new URLEditor(LABEL_WEBSITE(), null);
            editor.addClassName("website");
            return editor;
        },
            ce -> stringToURL(ce.getWebsiteLink(), null),
            (ce, url) -> ce.setWebsiteLink(urlToString(url))
        );

        if(_editMode == AbstractCompanyPropertyEditor.EditMode.StandardCompany)
        {
            final String superdomainName = _appUtil.getSite().getDefaultHostname().getName();
            final AtomicReference<TextEditor> domainNameEditor = new AtomicReference<>();
            final AtomicReference<Container> inputInstructionsRef = new AtomicReference<>();
            final AtomicReference<Container> customDomainInstructionsRef = new AtomicReference<>();
            final AtomicReference<Label> superdomainLabelRef = new AtomicReference<>();
            Function<String, String> convertDomainUIValue = val ->
            {
                if (!StringFactory.isEmptyString(val))
                {
                    String converted = val;
                    if (converted.endsWith('.' + superdomainName))
                        converted = converted.replace('.' + superdomainName, "");
                    converted = HOSTNAME_VALIDITY_PATTERN1.matcher(converted).replaceAll("-");
                    converted = HOSTNAME_VALIDITY_PATTERN2.matcher(converted).replaceAll("").toLowerCase();
                    return converted;
                }
                return val;
            };
            addEditorForProperty(() ->
                {
                    final TextEditor editor = new TextEditor(LABEL_SUB_DOMAIN(), null);
                    final Container inputInstructions = _uiHelper.createInputInstructions(
                        INSTRUCTIONS_SUB_DOMAIN(_terms.company()));
                    final Container customDomainInstructions = _uiHelper.createInputInstructions(
                        INSTRUCTIONS_CUSTOM_DOMAIN(_terms.company()));
                    final Label superdomainNameLabel = new Label(createText('.' + superdomainName), span, "super-domain-name");
                    editor.moveToTop(customDomainInstructions);
                    editor.moveToTop(inputInstructions);
                    editor.moveToTop(editor.getLabel());
                    editor.add(editor.getValueComponent());
                    editor.add(superdomainNameLabel);
                    editor.getValueComponent().addPropertyChangeListener(Field.PROP_TEXT, evt ->
                    {
                        if (editor.isEditable())
                        {
                            String uiValue = editor.getValueComponent().getText();
                            editor.getValueComponent().setText(convertDomainUIValue.apply(uiValue));
                        }
                    });

                    editor.setRequiredValueValidator();
                    domainNameEditor.set(editor);
                    inputInstructionsRef.set(inputInstructions);
                    customDomainInstructionsRef.set(customDomainInstructions);
                    superdomainLabelRef.set(superdomainNameLabel);
                    return editor;
                },
                ce ->
                {
                    final TextEditor editor = domainNameEditor.get();
                    String cehostname = ce.getHostname().getName();
                    if (cehostname == null) cehostname = "";
                    domainNameEditor.get().setEditable(!(!StringFactory.isEmptyString(cehostname)
                                                         && !cehostname.endsWith('.' + superdomainName)));
                    if (editor.isEditable())
                    {
                        cehostname = convertDomainUIValue.apply(cehostname.replace('.' + superdomainName, ""));
                    }
                    inputInstructionsRef.get().setVisible(editor.isEditable());
                    superdomainLabelRef.get().setVisible(editor.isEditable());
                    customDomainInstructionsRef.get().setVisible(!editor.isEditable());
                    return cehostname;
                },
                (ce, value) ->
                {
                    if (domainNameEditor.get().isEditable())
                        ce.getHostname().setName(String.join(".", value, superdomainName));
                    else
                        ce.getHostname().setName(ce.getHostname().getName());
                }
            );
        }

        addEditorForProperty(() -> {
                final URLEditor editor = new URLEditor(LABEL_LINKEDIN(), null);
                editor.addClassName("linkedin");
                return editor;
            },
            company -> stringToURL(company.getLinkedInLink(), null),
            (company, url) -> company.setLinkedInLink(urlToString(url))
            );

        addEditorForProperty(() -> {
            final URLEditor editor = new URLEditor(LABEL_TWITTER(), null);
            editor.addClassName("twitter");
            return editor;
        },
            company -> stringToURL(company.getTwitterLink(), null),
            (company, url) -> company.setTwitterLink(urlToString(url)));

        addEditorForProperty(() -> {
            final URLEditor editor = new URLEditor(LABEL_FACEBOOK(), null);
            editor.addClassName("facebook");
            return editor;
        },
            company -> stringToURL(company.getFacebookLink(), null),
            (company, url) -> company.setFacebookLink(urlToString(url)));
    }

    @Override
    public void setValue(@Nullable Company value)
    {
        super.setValue(value);

        if(value != null)
        {
            AppUtil.initialize(value);
        }

        if(isInited())
        {
            _webLogoEditor.setValue(Optional.ofNullable(value)
                .map(Company::getImage)
                .map(FileEntityFileItem::new)
                .orElse(null));

            _emailLogoEditor.setValue(Optional.ofNullable(value)
                .map(Company::getEmailLogo)
                .map(FileEntityFileItem::new)
                .orElse(null));
        }
    }

    @Override
    public void setEditable(boolean b)
    {
        super.setEditable(b);

        if(isInited())
        {
            _webLogoEditor.setEditable(b);
            _emailLogoEditor.setEditable(b);
        }
    }

    @Override
    public boolean validateUIValue(Notifiable notifiable)
    {
        final boolean valid = super.validateUIValue(notifiable);
        return _webLogoEditor.validateUIValue(notifiable)
               && _emailLogoEditor.validateUIValue(notifiable)
               && valid;
    }

    @Override
    public ModificationState getModificationState()
    {
        return AppUtil.getModificationStateForComponent(this);
    }

    /**
     * Gets picture editor.
     *
     * @return the picture editor
     */
    @Nonnull
    public VTCropPictureEditor getWebLogoEditor()
    {
        return Optional.ofNullable(_webLogoEditor).orElseThrow(() -> new IllegalStateException(
            "PictureEditor was null.  Do not call getWebLogoEditor before initialization!"));
    }

    /**
     * Gets picture editor.
     *
     * @return the picture editor
     */
    @Nonnull
    public VTCropPictureEditor getEmailLogoEditor()
    {
        return Optional.ofNullable(_emailLogoEditor).orElseThrow(() -> new IllegalStateException(
            "PictureEditor was null.  Do not call getEmailLogoEditor before initialization!"));
    }
}

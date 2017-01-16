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

package net.venturetech.sshd_test.profile.model.user;

import javax.annotation.Nonnull;

import net.proteusframework.core.locale.NamedObject;
import net.proteusframework.core.locale.TextSource;
import net.proteusframework.core.locale.annotation.I18N;
import net.proteusframework.core.locale.annotation.I18NFile;
import net.proteusframework.core.locale.annotation.L10N;

import static net.venturetech.sshd_test.profile.model.user.ContactMethodLOK.*;


/**
 * Enum defining different methods of contact.
 *
 * @author Alan Holt (aholt@venturetech.net)
 */
@I18NFile(
    symbolPrefix = "net.venturetech.sshd_test.profile.model.user.ContactMethod",
    i18n = {
        @I18N(symbol = "Phone Sms Name", l10n = @L10N("Text/SMS")),
        @I18N(symbol = "Phone Sms Description", l10n = @L10N("Allow notifications to be sent to my phone")),
        @I18N(symbol = "Email Name", l10n = @L10N("Email")),
        @I18N(symbol = "Email Description", l10n = @L10N("Allow notifications to be sent to my email address"))
    }
)
public enum ContactMethod implements NamedObject
{
    /** contact method */
    Email(EMAIL_NAME(), EMAIL_DESCRIPTION()),
    /** contact method */
    PhoneSms(PHONE_SMS_NAME(), PHONE_SMS_DESCRIPTION());

    private final TextSource _name;
    private final TextSource _description;

    ContactMethod(@Nonnull TextSource name, @Nonnull TextSource description)
    {
        _name = name;
        _description = description;
    }


    @Nonnull
    @Override
    public TextSource getName()
    {
        return _name;
    }

    @Nonnull
    @Override
    public TextSource getDescription()
    {
        return _description;
    }
}

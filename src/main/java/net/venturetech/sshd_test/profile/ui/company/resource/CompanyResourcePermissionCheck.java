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

package net.venturetech.sshd_test.profile.ui.company.resource;

import net.venturetech.sshd_test.profile.model.ProfileDAO;
import net.venturetech.sshd_test.profile.model.company.Company;
import net.venturetech.sshd_test.profile.model.user.User;
import net.venturetech.sshd_test.profile.service.MembershipOperationConfiguration;
import net.venturetech.sshd_test.profile.ui.ApplicationFunctions;
import net.venturetech.sshd_test.support.service.AppUtil;
import net.venturetech.sshd_test.support.service.ApplicationFunctionPermissionCheck;
import net.venturetech.sshd_test.support.ui.UIPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.proteusframework.internet.http.Request;
import net.proteusframework.ui.management.ApplicationRegistry;
import net.proteusframework.ui.management.link.RegisteredLinkDAO;
import net.proteusframework.users.model.Principal;

/**
 * Provides methods for checking permission to view the Company Resource page
 *
 * @author Alan Holt (aholt@venturetech.net)
 * @since 1/10/17
 */
@Service
public class CompanyResourcePermissionCheck implements ApplicationFunctionPermissionCheck
{
    @Autowired private UIPreferences _uiPreferences;
    @Autowired private ProfileDAO _profileDAO;
    @Autowired private MembershipOperationConfiguration _mop;
    @Autowired private RegisteredLinkDAO _registeredLinkDAO;
    @Autowired private ApplicationRegistry _applicationRegistry;
    @Autowired private AppUtil _appUtil;

    @Override
    public boolean checkPermissions(@Nonnull Request request, @Nullable User user)
    {
        if(functionExists(_appUtil.getSite(), request, _applicationRegistry, _registeredLinkDAO))
        {
            Company company = _uiPreferences.getSelectedCompany();

            return _profileDAO.canOperate(user, company, AppUtil.UTC, _mop.viewRepositoryResources());
        }
        return false;
    }

    @Override
    public boolean checkPermissions(@Nonnull Request request, @Nullable Principal principal)
    {
        return false; //We just use the User
    }

    @Override
    public String getApplicationFunctionName()
    {
        return ApplicationFunctions.Company.Resource.MANAGEMENT;
    }
}

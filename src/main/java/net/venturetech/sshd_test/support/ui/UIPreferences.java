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

package net.venturetech.sshd_test.support.ui;

import net.venturetech.sshd_test.profile.model.company.Company;
import net.venturetech.sshd_test.profile.model.company.CompanyDAO;
import net.venturetech.sshd_test.profile.model.terminology.FallbackProfileTermProvider;
import net.venturetech.sshd_test.profile.model.terminology.ProfileTermProvider;
import net.venturetech.sshd_test.profile.model.user.User;
import net.venturetech.sshd_test.profile.model.user.UserDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import net.proteusframework.core.hibernate.dao.EntityRetriever;
import net.proteusframework.core.notification.Notification;
import net.proteusframework.internet.http.Hostname;
import net.proteusframework.internet.http.SiteContext;

/**
 * UI Preferences that are limited to SESSION scope
 *
 * @author Alan Holt (aholt@venturetech.net)
 * @since 1/25/16 1:51 PM
 */
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UIPreferences
{
    private static final Logger _logger = LogManager.getLogger(UIPreferences.class);
    @Autowired private SiteContext _siteContext;
    @Autowired private CompanyDAO _companyDAO;
    @Autowired private UserDAO _userDAO;
    @Autowired private EntityRetriever _entityRetriever;

    private final HashMap<String, Integer> _intPrefs = new HashMap<>();
    private final HashMap<String, Object> _objPrefs = new HashMap<>();
    private final List<Notification> _messages = new ArrayList<>();
    private final HashMap<String, Object> _urlParams = new HashMap<>();

    private Company _selectedCompany;
    private User _currentUser;
    private ProfileTermProvider _selectedCompanyTermProvider;

    /**
     * Add a message to be stored within UIPreferences.  It will be consumed upon calling {@link UIPreferences#consumeMessages()}
     *
     * @param message the message to add
     */
    public void addMessage(Notification message)
    {
        _messages.add(message);
    }

    /**
     * Clears the messages within UIPreferences, returning them.
     *
     * @return messages
     */
    public List<Notification> consumeMessages()
    {
        List<Notification> messages = new ArrayList<>(_messages);
        _messages.clear();
        return messages;
    }

    /**
     * Get a stored Integer within UIPreferences
     *
     * @param key the Key to retrieve
     *
     * @return the stored value.
     */
    public Optional<Integer> getStoredInteger(String key)
    {
        return Optional.ofNullable(_intPrefs.get(key));
    }

    /**
     * Get a stored Object within UIPreferences
     *
     * @param key the Key to retrieve
     *
     * @return the stored value.
     */
    public Optional<Object> getStoredObject(String key)
    {
        return Optional.ofNullable(_objPrefs.get(key));
    }

    /**
     * Set a stored Integer within UIPreferences
     *
     * @param key the Key to store
     * @param value the value to store
     */
    public void setStoredInteger(String key, @Nullable Integer value)
    {
        _intPrefs.put(key, value);
    }

    /**
     * Set a stored Object within UIPreferences
     *
     * @param key the Key to store
     * @param value the value to store
     */
    public void setStoredObject(String key, @Nullable Object value)
    {
        _objPrefs.put(key, value);
    }

    /**
     * Add a URL parameter to be consumed when {@link #consumeURLParams()} is called.
     *
     * @param param the param
     * @param value the value
     */
    public void addURLParam(String param, Object value)
    {
        _urlParams.put(param, value);
    }

    /**
     * Clears the stored URL parameters within UIPreferences, returning them.
     *
     * @return the hash map
     */
    public HashMap<String, Object> consumeURLParams()
    {
        HashMap<String, Object> params = new HashMap<>(_urlParams);
        _urlParams.clear();
        return params;
    }

    /**
     * Gets the selected {@link Company} based on the current Hostname.
     * If no company exists for the current Hostname, then null is returned.
     *
     * @return the company for current user
     */
    public synchronized Company getSelectedCompany()
    {
        if(_selectedCompany == null)
        {
            //Get the Company from the hostname of the Request.
            Hostname hostname = _siteContext.getRequestedHostname();
            _selectedCompany = _companyDAO.getCompanyForHostname(hostname);
            if(_selectedCompany == null)
                _logger.error("Unable to determine Company for hostname: " + hostname.getName());
            return _selectedCompany;
        }
        else
        {
            return _entityRetriever.reattachIfNecessary(_selectedCompany);
        }
    }

    /**
     * Sets the selected Company for current user.
     *
     * @param company the company
     */
    public synchronized void setSelectedCompany(@Nonnull Company company)
    {
        _selectedCompany = company;
        _selectedCompanyTermProvider = null;
    }

    /**
     * Gets company term provider.
     *
     * @return the company term provider.
     */
    public synchronized ProfileTermProvider getSelectedCompanyTermProvider()
    {
        if(_selectedCompanyTermProvider == null)
        {
            return _selectedCompanyTermProvider = new FallbackProfileTermProvider(
                Optional.ofNullable(getSelectedCompany()).map(Company::getProfileTerms).orElse(null));
        }
        else
        {
            return _selectedCompanyTermProvider;
        }
    }

    /**
     * Get the current user.
     *
     * @return the current user.
     */
    public synchronized User getCurrentUser()
    {
        if(_currentUser != null)
        {
            return _entityRetriever.reattachIfNecessary(_currentUser);
        }
        else
        {
            return _currentUser = _userDAO.getAssertedCurrentUser();
        }
    }
}

/*
 *
 *  * Copyright 2015-2016 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.yqboots.security.web.controller;

import com.yqboots.security.core.User;
import com.yqboots.security.core.UserExistsException;
import com.yqboots.security.core.UserManager;
import com.yqboots.security.core.UserNotFoundException;
import com.yqboots.security.web.form.UserForm;
import com.yqboots.security.web.form.UserFormConverter;
import com.yqboots.web.support.WebKeys;
import com.yqboots.web.form.SearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller for {@link User}.
 *
 * @author Eric H B Zhan
 * @since 1.1.0
 */
@Controller
@RequestMapping(value = "/security/user")
@SessionAttributes(names = {WebKeys.SEARCH_FORM})
public class UserController {
    private static final String REDIRECT_VIEW_PATH = "redirect:/security/user";
    private static final String VIEW_HOME = "security/user/index";
    private static final String VIEW_FORM = "security/user/form";

    @Autowired
    private UserManager userManager;

    @ExceptionHandler(value = {UserExistsException.class, UserNotFoundException.class})
    protected void handleException(Exception ex, BindingResult bindingResult) {
        bindingResult.reject(ex.getMessage());
    }

    @ModelAttribute(WebKeys.SEARCH_FORM)
    protected SearchForm<String> searchForm() {
        return new SearchForm<>();
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String list(@ModelAttribute(WebKeys.SEARCH_FORM) final SearchForm<String> searchForm,
                       @PageableDefault final Pageable pageable,
                       final ModelMap model) {
        model.addAttribute(WebKeys.PAGE, userManager.findUsers(searchForm.getCriterion(), pageable));
        return VIEW_HOME;
    }

    @RequestMapping(params = {WebKeys.ACTION_NEW}, method = RequestMethod.GET)
    public String preAdd(final ModelMap model) {
        model.addAttribute(WebKeys.MODEL, new UserForm());
        return VIEW_FORM;
    }

    @RequestMapping(params = {WebKeys.ID, WebKeys.ACTION_UPDATE}, method = RequestMethod.GET)
    public String preUpdate(@RequestParam final Long id, final ModelMap model) {
        User user = userManager.findUser(id);

        model.addAttribute(WebKeys.MODEL, new UserFormConverter().convert(user));
        return VIEW_FORM;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute(WebKeys.MODEL) final UserForm domain,
                         final BindingResult bindingResult,
                         final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return VIEW_FORM;
        }

        // create new
        if (!domain.isExisted()) {
            User user = new User();
            user.setUsername(domain.getUsername());
            userManager.addUser(user);
        }

        userManager.updateGroups(domain.getUsername(), domain.getGroups());
        userManager.updateRoles(domain.getUsername(), domain.getRoles());

        model.clear();

        return REDIRECT_VIEW_PATH;
    }

    @RequestMapping(params = {WebKeys.ID, WebKeys.ACTION_DELETE}, method = RequestMethod.GET)
    public String delete(@RequestParam final Long id, final ModelMap model) {
        userManager.removeUser(id);
        model.clear();

        return REDIRECT_VIEW_PATH;
    }
}
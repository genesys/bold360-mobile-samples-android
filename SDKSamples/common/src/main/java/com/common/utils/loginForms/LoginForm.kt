package com.common.utils.loginForms

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

abstract class LoginForm : Fragment() {
    protected val loginFormViewModel: LoginFormViewModel by activityViewModels()

    open fun hasFormParam(flag: Int): Boolean {
        return loginFormViewModel.formsParams and flag == flag
    }
}
package com.common.utils.loginForms

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.common.utils.loginForms.dynamicFormPOC.LoginFormViewModel

abstract class LoginForm : Fragment() {
    protected val loginFormViewModel: LoginFormViewModel by activityViewModels()

    open fun hasFormParam(flag: Int): Boolean {
        return false//loginFormViewModel.formsParams and flag == flag
    }
}
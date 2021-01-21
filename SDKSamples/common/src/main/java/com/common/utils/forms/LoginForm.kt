package com.common.utils.forms

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.common.topicsbase.LoginFormViewModel

abstract class LoginForm : Fragment() {
    protected val loginFormViewModel: LoginFormViewModel by activityViewModels()
}
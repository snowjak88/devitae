import React, { useState } from 'react';
import { AxiosError } from "axios";

import {
        Button,
        Dialog, DialogActions, DialogContent, DialogTitle
    } from '@material-ui/core';
import LockIcon from '@material-ui/icons/Lock';
import LockOpenIcon from '@material-ui/icons/LockOpen';

import { Authentication, AuthenticationContext } from './Authentication';
import { LoginForm, LoginFormContents } from './LoginForm';

/*
 * Exposes a Log-In/-Out button.
 * Activating the button when the user is *not* authenticated will open a dialog containing a LoginForm.
 */

type LoginState = {
    loginPopupVisible: boolean;
    loginPopupError?: string;
    form?: LoginFormContents;
};

type LoginButtonProps = {};

export const LoginButton = (props:LoginButtonProps) => {

    const [loginState, setLoginState] = useState({ loginPopupVisible: false } as LoginState);

    const showLoginPopup = () => {
        setLoginState({ loginPopupVisible: true, loginPopupError: undefined, form: undefined });
    };

    const hideLoginPopup = () => {
        setLoginState({ loginPopupVisible: false, loginPopupError: undefined, form: undefined });
    };

    const onLoginFormUpdate = (form: LoginFormContents) => {
        setLoginState({ ...loginState, form: form });
    };

    const onLoginSubmit = (auth: Authentication) => {
        auth.login( loginState.form?.username as string, loginState.form?.password as string )
            .then( hideLoginPopup )
            .catch( (error) => {
                    const status = error?.response?.data?.status;
                    let errorMessage: string;
                    switch(status) {
                        case 401:
                            errorMessage = 'Invalid username or password.';
                            break;
                        case 403:
                            errorMessage = 'Given account is locked.';
                            break;
                        case 408:
                            errorMessage = 'Login request timed out. Application might be down.';
                            break;
                        default:
                            errorMessage = `An unexpected error (${status || "?"}) occurred. Please try again later.`;
                    }
                    setLoginState({ ...loginState, loginPopupError: errorMessage });
            });
    };

    return (
        <AuthenticationContext.Consumer>
            { (auth:Authentication) => (
                <>
                    <Button color="primary"
                            startIcon={(auth.authenticated) ? (<LockIcon />) : (<LockOpenIcon />) }
                            onClick={() => (auth.authenticated) ? auth.logout() : showLoginPopup() }>
                        Log {auth.authenticated ? 'out' : 'in'}
                    </Button>
                    <Dialog open={loginState.loginPopupVisible}
                            onClose={hideLoginPopup}
                            aria-labelledby="login-popup-title">
                        <DialogTitle id="login-popup-title">Log In</DialogTitle>
                        <DialogContent>
                            <LoginForm onUpdate={onLoginFormUpdate} errorMessage={loginState.loginPopupError} />
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={() => onLoginSubmit(auth)} color="primary">Submit</Button>
                            <Button onClick={hideLoginPopup} color="secondary">Cancel</Button>
                        </DialogActions>
                    </Dialog>
                </>
            ) }
        </AuthenticationContext.Consumer>
    );
}
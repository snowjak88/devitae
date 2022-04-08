import React, { useState, ChangeEvent } from 'react';

import {
        Button,
        Dialog, DialogActions, DialogContent, DialogTitle,
        TextField
    } from '@material-ui/core';
import LockIcon from '@material-ui/icons/Lock';
import LockOpenIcon from '@material-ui/icons/LockOpen';

import { Authentication, AuthenticationContext } from './Authentication';

/*
 * Exposes a Log-In/-Out button.
 * Activating the button when the user is *not* authenticated will open a dialog for the user to enter his credentials.
 * Activating the button when the user is authenticated will call Authentication.logout() (i.e., will clear the
 * user's authentication-info).
 */

type LoginState = {
    loginPopupVisible: boolean;
    loginPopupError?: string;
    username?: string;
    password?: string;
};

type LoginButtonProps = {};

export const LoginButton = (props:LoginButtonProps) => {

    const [loginState, setLoginState] = useState({ loginPopupVisible: false } as LoginState);

    const showLoginPopup = () => {
        setLoginState({ loginPopupVisible: true, loginPopupError: undefined, username: undefined, password: undefined });
    };

    const hideLoginPopup = () => {
        setLoginState({ loginPopupVisible: false, loginPopupError: undefined, username: undefined, password: undefined });
    };

    const onLoginFormUpdate = (event: ChangeEvent<HTMLInputElement>) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        setLoginState({ ...loginState, [name]: value });
    };

    const onLoginSubmit = (auth: Authentication) => {
        auth.login(loginState.username as string, loginState.password as string)
            .then(() => {
                hideLoginPopup();
            })
            .catch((error) => {
                setLoginState({ ...loginState, loginPopupError: error.message });
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
                            <form noValidate autoComplete="off">
                                <TextField autoFocus
                                           id="name" name="username" label="Username"
                                           error={(loginState.loginPopupError !== undefined)}
                                           value={loginState.username} onChange={onLoginFormUpdate}
                                           fullWidth />
                                <TextField id="password" name="password" label="Password" type="password"
                                           error={(loginState.loginPopupError !== undefined)}
                                           value={loginState.password} onChange={onLoginFormUpdate}
                                           fullWidth />
                            </form>
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
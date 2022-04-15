import React, { useState, useContext } from 'react';
import { AxiosError } from "axios";

import {
        Button,
        Dialog, DialogActions, DialogContent, DialogTitle
    } from '@material-ui/core';
import LockIcon from '@material-ui/icons/Lock';
import LockOpenIcon from '@material-ui/icons/LockOpen';

import { Authentication, AuthenticationContext } from './Authentication';
import { LoginDialog } from './LoginDialog';

/*
 * Exposes a Log-In/-Out button.
 *
 * Activating the button when the user is *not* authenticated will open a LoginDialog.
 * Note that you must wrap this in an AuthenticationContextProvider to ensure correct function.
 */

type LoginButtonProps = {};

export const LoginButton = (props:LoginButtonProps) => {

    const auth = useContext(AuthenticationContext);

    const [ loginDialogOpen, setLoginDialogOpen ] = useState(false);
    const [ loginDialogError, setLoginDialogError ] = useState<string | undefined>(undefined);

    const handleLoginDialogShow = () => {
        setLoginDialogOpen(true);
    }

    const handleLoginDialogSubmit = (username: string, password: string) => {
        auth.login(username, password)
            .then(() => {
                setLoginDialogOpen(false);
            })
            .catch((error) => {
                const status = error?.response?.status;
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
                setLoginDialogError(errorMessage);
            });
    };

    const handleLoginDialogClose = () => {
        setLoginDialogOpen(false);
    };

    return (
        <>
            <Button color="primary"
                    startIcon={(auth.authenticated) ? (<LockIcon />) : (<LockOpenIcon />) }
                    onClick={() => (auth.authenticated) ? auth.logout() : handleLoginDialogShow() }>
                Log {auth.authenticated ? 'out' : 'in'}
            </Button>
            <LoginDialog open={loginDialogOpen}
                        errorMessage={loginDialogError}
                        onSubmit={handleLoginDialogSubmit}
                        onClose={handleLoginDialogClose}/>
        </>
    );
}
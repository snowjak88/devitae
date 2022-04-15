import { useState, useContext, MouseEvent } from 'react';

import {
        IconButton,
        ListItemIcon, ListItemText,
        Menu, MenuItem
    } from '@material-ui/core';
import AccountBoxIcon from '@material-ui/icons/AccountBox';
import AccountCircleIcon from '@material-ui/icons/AccountCircle';
import LockIcon from '@material-ui/icons/Lock';
import LockOpenIcon from '@material-ui/icons/LockOpen';
import SettingsIcon from '@material-ui/icons/Settings';

import { Authentication, AuthenticationContext } from '../auth/Authentication';
import { LoginDialog } from '../auth/LoginDialog';

/*
 * Presents an IconButton and Menu that together implement the "login/logout" menu.
 *
 * When the user clicks the button, the popup menu is displayed, and presents a list of options:
 * - "Profile" (if logged in, redirects the user to the "view profile" page)
 * - "Account Settings" (if logged in, redirects the user to the "account settings" page)
 * - "Logout" (if logged in, calls Authentication.logout() on the containing AuthenticationContext)
 * - "Login" (if the user is logged out, shows a LoginDialog)
 *
 * Note that you must wrap this in an AuthenticationContextProvider, to ensure correct function.
 */

type LoginButtonMenuProps = {
};

export const LoginButtonMenu = (props:LoginButtonMenuProps) => {

    const auth = useContext(AuthenticationContext);

    const [ anchorEl, setAnchorEl ] = useState<null | HTMLElement>(null);
    const menuOpen = Boolean(anchorEl);

    const [ loginDialogOpen, setLoginDialogOpen ] = useState(false);
    const [ loginDialogError, setLoginDialogError ] = useState<string | undefined>(undefined);

    const handleMenuOpen = (event: MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleMenuClose = () => {
        setAnchorEl(null);
    };

    const handleMenuLogin = () => {
        showLoginDialog();
        handleMenuClose();
    };

    const handleMenuProfile = () => {
        handleMenuClose();
    };

    const handleMenuSettings = () => {
        handleMenuClose();
    };

    const handleMenuLogout = () => {
        auth.logout();
        handleMenuClose();
    };

    const showLoginDialog = () => {
        setLoginDialogOpen(true);
        setLoginDialogError(undefined);
    };

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
            <IconButton color="inherit" onClick={handleMenuOpen}
                    aria-label="authentication-menu"
                    aria-controls="menu-login"
                    aria-haspopup="true">
                <AccountBoxIcon />
            </IconButton>
            <Menu id="menu-login" anchorEl={anchorEl} keepMounted
                    open={menuOpen}
                    onClose={handleMenuClose}>
                { (auth.authenticated) && (
                    <>
                        <MenuItem onClick={handleMenuProfile} button>
                            <ListItemIcon><AccountCircleIcon /></ListItemIcon>
                            <ListItemText primary="Profile" />
                        </MenuItem>
                        <MenuItem onClick={handleMenuSettings} button divider>
                            <ListItemIcon><SettingsIcon /></ListItemIcon>
                            <ListItemText primary="Settings" />
                        </MenuItem>
                        <MenuItem onClick={handleMenuLogout} button>
                            <ListItemIcon><LockIcon /></ListItemIcon>
                            <ListItemText primary="Logout" />
                        </MenuItem>
                    </>
                    ) }
                { (!auth.authenticated) && (
                    <MenuItem onClick={handleMenuLogin} button>
                        <ListItemIcon><LockOpenIcon /></ListItemIcon>
                        <ListItemText primary="Login" />
                    </MenuItem>
                    ) }
            </Menu>
            <LoginDialog open={loginDialogOpen}
                        errorMessage={loginDialogError}
                        onSubmit={handleLoginDialogSubmit}
                        onClose={handleLoginDialogClose}/>
        </>
    );
};
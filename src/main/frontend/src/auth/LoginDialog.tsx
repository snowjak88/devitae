import { useState, useContext } from 'react';

import {
        Button,
        Dialog, DialogContent, DialogTitle, DialogActions
    } from '@material-ui/core';

import { AuthenticationContext, Authentication } from './Authentication';
import { LoginForm, LoginFormContents } from './LoginForm';

/*
 * Presents the Login dialog to the user.
 *
 * Note that this component will *not* handle actually logging-in for you. You must handle
 * that yourself using the callback provided in the component props.
 */

type LoginDialogProps = {
    open: boolean;
    errorMessage?: string;
    onSubmit: (username: string, password: string) => void;
    onClose: () => void;
};

export const LoginDialog = (props:LoginDialogProps) => {

    const auth = useContext(AuthenticationContext);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const onLoginFormUpdate = (form: LoginFormContents) => {
        setUsername(form.username);
        setPassword(form.password);
    };

    const onSubmit = () => {
        props.onSubmit(username, password);
    };

    return (
        <Dialog open={props.open}
                onClose={props.onClose}
                aria-labelledby="login-dialog-title">
            <DialogTitle id="login-dialog-title">Log In</DialogTitle>
            <DialogContent>
                <LoginForm onUpdate={onLoginFormUpdate} errorMessage={props.errorMessage} />
            </DialogContent>
            <DialogActions>
                <Button onClick={onSubmit} color="primary">Submit</Button>
                <Button onClick={props.onClose} color="secondary">Cancel</Button>
            </DialogActions>
        </Dialog>
    );
};
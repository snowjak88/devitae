import { useState, ChangeEvent, ReactNode } from 'react';

import {
        Button, IconButton,
        TextField,
        FormControl, FormHelperText,
        Input, InputLabel, InputAdornment
    } from '@material-ui/core';

import VisibilityIcon from '@material-ui/icons/Visibility';
import VisibilityOffIcon from '@material-ui/icons/VisibilityOff';


/*
 * Exposes a login form.
 *
 * Users must register their own form-update listener with props.onUpdate, of the form:
 *   ( { username: string, password: string } ) => void
 * or, equivalently,
 *   ( LoginFormContents ) => void
 *
 * Importantly, LoginForm does *not* handle any form-submission, nor does it render
 * any buttons. It *only* manages the form input-fields.
 */

export type LoginFormContents = {
    username: string,
    password: string
};

type LoginFormState = {
    username: string,
    password: string,
    passwordVisible: boolean
};

type LoginFormProps = {
    onUpdate: (contents: LoginFormContents) => void,
    errorMessage?: string
};

export const LoginForm = (props: LoginFormProps) => {

    const [ formState, setFormState ] = useState({
        username: '',
        password: '',
        passwordVisible: false
    } as LoginFormState);

    const onLoginFormUpdate = (event: ChangeEvent<HTMLInputElement>) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        props.onUpdate({ ...{ username: formState.username, password: formState.password }, [name]: value });
        setFormState({ ...formState, [name]: value });
    };

    return (
        <form noValidate autoComplete="off">
            <TextField autoFocus
                       id="name" name="username" label="Username"
                       error={(props.errorMessage !== undefined)}
                       value={formState.username} onChange={onLoginFormUpdate} />
            <FormControl error={(props.errorMessage !== undefined)}>
                <InputLabel htmlFor="password">Password</InputLabel>
                <Input id="password" name="password" type={ (formState.passwordVisible) ? "text" : "password"}
                       value={formState.password} onChange={onLoginFormUpdate}
                       endAdornment={
                           <InputAdornment position="end">
                               <IconButton
                                   aria-label="toggle password visibility"
                                   onClick={() => setFormState({ ...formState, passwordVisible: !formState.passwordVisible })}>
                                   {formState.passwordVisible ? <VisibilityIcon /> : <VisibilityOffIcon />}
                               </IconButton>
                           </InputAdornment>
                       } />
                <FormHelperText>{props.errorMessage}</FormHelperText>
            </FormControl>
        </form>
    );

}
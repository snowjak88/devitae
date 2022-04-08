import { createContext, useContext, useState, ReactNode } from 'react';

import axios, { AxiosError } from "axios";

/*
 * Provides:
 *  - Authentication
 *      Defines what an authentication-context looks like: username, scopes/permissions, the current token, etc.useContext
 *  - AuthenticationContext
 *      The Context you should be Consuming in your components.
 *  - AuthenticationContextProvider
 *      A pre-configured AuthenticationContext.Provider, complete with configured methods for:
 *      - logging in
 *
 * Example usage:
 *
 * import { Button } from "@material-ui/core";
 * import { Authentication, AuthenticationContext, AuthenticationContextProvider } from './Authentication';
 *
 * const MyComponent = () => {
 *     return (
 *         <AuthenticationContextProvider>
 *             ...
 *             <AuthenticationContext.Consumer>
 *                 { (auth:Authentication) => ( <Button onClick={() => auth.login('my-username', 'my-password')}>Log In</Button> ) }
 *             </AuthenticationContext.Consumer>
 *             ...
 *         </AuthenticationContextProvider>
 *         );
 * }
 *
 * Notes on Authentication.login(...):
 *  - This function yields a Promise representing the outcome of the login-process.AuthenticationContext
 *    Obviously you can hook onto that using Promise.then(...) to, e.g., close a login-dialog.
 *    But you also can, and should, listen for login-failures using Promise.catch().
 *    Most notably, to access the returned HTTP-code, use code of the form:
 *      ...
 *      auth.login( myUsername, myPassword )
 *          .catch( (error) => console.log( `HTTP code = ${error.response.data.status}` ) );
 *      ...
 */

export type Authentication = {
    authenticated: boolean;
    username?: string;
    scopes: string[];
    jwt?: string;
    login: (username: string, password: string, onError?:(code: number) => void) => Promise<void>;
    logout: () => void;
};

export const AuthenticationContext = createContext<Authentication>({
    authenticated: false,
    username: undefined,
    scopes: [],
    jwt: undefined,
    login: (username: string, password: string, onError?:(code: number) => void) => Promise.resolve(),
    logout: () => {}
});

type AuthenticationProviderProps = {
    children: ReactNode[] | ReactNode;
};

export const AuthenticationContextProvider = (props: AuthenticationProviderProps) => {
    const [auth, setAuth] = useState<Authentication>({
        authenticated: false,
        username: undefined,
        scopes: [],
        jwt: undefined,
        login: (username: string, password: string, onError?:(code: number) => void) => {
            return axios
                .post("/login", { username, password })
                .then(response => {
                    console.log(`Login successful: '${JSON.stringify(response)}'`);
                    setAuth({ ...auth, ...response.data });
                });
        },
        logout: () => {
            setAuth({ ...auth, authenticated: false, username: undefined, scopes: [], jwt: undefined });
        }
    });

    return(
        <AuthenticationContext.Provider value={auth}>
            {props.children}
        </AuthenticationContext.Provider>
    );
}
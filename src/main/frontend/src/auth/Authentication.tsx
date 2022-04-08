import { createContext, useContext, useState, ReactNode } from 'react';

import axios from "axios";

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
 */

export type Authentication = {
    authenticated: boolean;
    username?: string;
    scopes: string[];
    jwt?: string;
    login: (username: string, password: string) => Promise<void>;
    logout: () => void;
};

export const AuthenticationContext = createContext<Authentication>({
    authenticated: false,
    username: undefined,
    scopes: [],
    jwt: undefined,
    login: (username: string, password: string) => Promise.resolve(),
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
        login: (username: string, password: string) => {
            return axios
                .post("/login", { username, password })
                .then(response => {
                    console.log(`Login response: ${JSON.stringify(response)}`);
                    console.log(`Setting auth to ${JSON.stringify({ ...auth, ...response.data })}`);
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
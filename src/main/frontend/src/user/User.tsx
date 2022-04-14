import { ReactNode, createContext, useContext, useEffect, useLayoutEffect, useState } from 'react';

import axios from 'axios';

import { Authentication, AuthenticationContext } from '../auth/Authentication';

/*
 * Provides:
 *  - User
 *      Defines what a current-user context looks like: username, scopes/permissions, etc.
 *  - UserContext
 *      What you should be Consuming in your components.
 *  - UserContextProvider
 *      A pre-configured UserContext.Provider, complete with configured methods for:
 *      - fetch(): fetching the current user
 *      - clear(): clear this context of its current user
 *      You must provide an Authentication object to this provider when instantiating it.
 *
 * Example usage:
 *
 * import { AuthenticationContext } from './auth/Authentication';
 * import { User, UserContext, UserContextProvider } from './user/User';
 *
 * const MyComponent = () => {
 *     return (
 *          <UserContextProvider id={1}>
     *          ...
     *          <UserContext.Consumer>
     *              { (user:User) => (
     *                  <span>My username is {user.username}, and I was created on {user.created}</span>
     *              ) }
     *          </UserContext.Consumer>
     *          ...
     *      </UserContextProvider>
 *         );
 */

export type User = {
    id: number;
    username?: string;
    scopes?: string[];
    created?: Date;
    fetch?: (id:number, auth:Authentication) => Promise<void>;
    clear?: () => void;
};

export const UserContext = createContext<User>({
    id: -1,
    fetch: (id:number, auth:Authentication) => Promise.resolve(),
    clear: () => {}
});

type UserProviderProps = {
    id: number,
    children: ReactNode | ReactNode[]
};

export const UserContextProvider = (props:UserProviderProps) => {

    const [user, setUser] = useState<User>({
        id: props.id,
        username: undefined,
        scopes: undefined,
        created: undefined,
        fetch: (id:number, auth:Authentication) => {
            console.log(`Fetching user ${id} ...`);
            return axios.get(`/user/${id}`, { headers: { Authorization: `Bearer ${auth.jwt}` } })
                .then(response => {
                    setUser({
                        ...user,
                        id: response.data.id,
                        username: response.data.username,
                        created: response.data.created,
                        scopes: response.data.scopes
                    });
                });
        },
        clear: () => {
            setUser({
                id: -1,
                username: undefined,
                scopes: undefined,
                created: undefined
            })
        }
    });

    const auth = useContext(AuthenticationContext);

    useEffect(() => { user.fetch?.(props.id, auth); }, [props.id, auth.jwt]);

    return (
            <UserContext.Provider value={user}>
                {props.children}
            </UserContext.Provider>
            );
};
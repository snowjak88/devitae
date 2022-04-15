import { useState } from 'react';
import { BrowserRouter as Router } from "react-router-dom";

import { Authentication, AuthenticationContext, AuthenticationContextProvider } from "./auth/Authentication";
import { AppNavBar } from "./nav/AppNavBar";
import { UserContextProvider } from "./user/User";
import { UserDetailsView } from "./user/UserDetailsView";
import { UserBriefView } from "./user/UserBriefView";

type ApplicationProp = {}

const Application = (props: ApplicationProp) => {

    const [ selectedUser, setSelectedUser ] = useState<number|undefined>( undefined );

    return (
        <Router>
            <AuthenticationContextProvider>
                <AppNavBar />

                <AuthenticationContext.Consumer>
                    {(auth:Authentication) => (
                        <UserContextProvider id={auth.id}>
                            <UserBriefView onClick={() => setSelectedUser(auth.id)} />
                        </UserContextProvider>
                    )}
                </AuthenticationContext.Consumer>

                <UserContextProvider id={selectedUser}>
                    <UserDetailsView />
                </UserContextProvider>

            </AuthenticationContextProvider>
        </Router>
    );
};

export default Application;

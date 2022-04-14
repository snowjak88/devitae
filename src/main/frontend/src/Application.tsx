import React, { useState, useEffect } from "react";
import { BrowserRouter as Router } from "react-router-dom";

import { Button } from "@material-ui/core";

import { Authentication, AuthenticationContext, AuthenticationContextProvider } from "./auth/Authentication";
import { User, UserContext, UserContextProvider } from "./user/User";
import { LoginButton } from "./auth/LoginButton";

type ApplicationProp = {}

const Application = (props: ApplicationProp) => {

    return (
        <Router>
            <AuthenticationContextProvider>
                <h1>Hallo</h1>
                <AuthenticationContext.Consumer>
                    {(auth:Authentication) => (
                        <>
                            <p>You are {auth.authenticated ? `currently authenticated as user-ID ${auth.id}` : "not authenticated" }.</p>
                            <UserContextProvider id={auth.id}>
                                <UserContext.Consumer>
                                    {(user:User) => (
                                        <p>Your username is {user.username}, and you were created on {user.created}.</p>
                                    )}
                                </UserContext.Consumer>
                            </UserContextProvider>
                        </>
                    )}
                </AuthenticationContext.Consumer>
                <LoginButton />
            </AuthenticationContextProvider>
        </Router>
    );
};

export default Application;

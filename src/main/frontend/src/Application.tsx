import React, { useState, useEffect } from "react";
import { BrowserRouter as Router } from "react-router-dom";

import { Button } from "@material-ui/core";

import { Authentication, AuthenticationContext, AuthenticationContextProvider } from "./auth/Authentication";
import { LoginButton } from "./auth/LoginButton";

type ApplicationProp = {}

const Application = (props: ApplicationProp) => {

    return (
        <Router>
            <AuthenticationContextProvider>
                <h1>Hallo</h1>
                <AuthenticationContext.Consumer>
                    {(auth:Authentication) => (<p>You are {auth.authenticated ? `currently authenticated as ${auth.username}` : "not authenticated" }.</p>)}
                </AuthenticationContext.Consumer>
                <LoginButton />
            </AuthenticationContextProvider>
        </Router>
    );
};

export default Application;

import React, { useState, useEffect } from "react";
import { BrowserRouter as Router } from "react-router-dom";

import { Button } from "@material-ui/core";

import axios from "axios";

import { Authentication } from "./auth/Authentication";

type ApplicationProp = {}

const Application = (props: ApplicationProp) => {

    const [auth, setAuth] = useState( { "authenticated": false, "username": "", scopes: [], jwt: undefined } as Authentication );

    const jwtSubscription = useEffect( () => {
        axios
            .get("/auth", (auth.jwt ? { headers: { Authorization: `Bearer ${auth.jwt}` } } : {}) )
            .then((response) => { setAuth( { ...auth, ...response.data } ); });
    }, [auth.jwt] );

    const doLogin = () => {
        axios
            .post("/login", { username: "user", password: "password" } )
            .then((response) => {
                setAuth( { ...auth, ...response.data } );
            });
    };

    return (
        <Router>
            <h1>Hallo</h1>
            <p>You are {auth.authenticated ? `currently authenticated as ${auth.username}` : "not authenticated" }.</p>
            <Button onClick={doLogin}>Log-in</Button>
        </Router>
    );
};

export default Application;

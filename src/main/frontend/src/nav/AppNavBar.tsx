import { useState, useContext } from 'react';

import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';
import {
        AppBar, Toolbar,
        IconButton,
        Typography
    } from '@material-ui/core';

import { Authentication, AuthenticationContext, AuthenticationContextProvider } from '../auth/Authentication';
import { LoginButtonMenu } from './LoginButtonMenu';

/*
 * Presents the application-wide nav bar.
 *
 * Note that this should be wrapped inside of an AuthenticationContextProvider to ensure correct function.
 */

const useStyles = makeStyles((theme:Theme) =>
    createStyles({
        root: {
            flexGrow: 1,
        },
        title: {
            flexGrow: 1,
        },
    }));

type AppNavBarProps = {
};

export const AppNavBar = (props:AppNavBarProps) => {

    const classes = useStyles();

    return(
        <AppBar position="static" className={classes.root}>
            <Toolbar>
                <Typography variant="h5" className={classes.title}>DeVitae</Typography>
                <LoginButtonMenu />
            </Toolbar>
        </AppBar>
    );
};
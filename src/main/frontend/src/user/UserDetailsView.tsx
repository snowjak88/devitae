import { useState, useContext } from 'react';

import { Paper, Typography } from '@material-ui/core';
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';

import { User, UserContext } from './User';
import { UserAvatar } from './UserAvatar';

/*
 * Presents a view of a user's details.
 * Typically, this would be shown only to the user himself, or to an admin with the appropriate permission.
 *
 * Note: you must wrap this inside of a <UserContextProvider> for this to work properly.
 */

const useStyles = makeStyles((theme:Theme) =>
    createStyles({
        root: {
            padding: theme.spacing(2),
            margin: theme.spacing(2)
        },
        title: {
            display: 'inline-flex',
            marginBottom: theme.spacing(2)
        }
    }));

type UserDetailsViewProps = {};

export const UserDetailsView = (props:UserDetailsViewProps) => {

    const user = useContext(UserContext);
    const classes = useStyles();

    if(user.username === undefined)
        return (
            <Typography>
                (none)
            </Typography>
        );

    return(
        <Paper className={classes.root}>
            <UserAvatar user={user}/>
            <Typography variant="h6" component="span" className={classes.title} gutterBottom>
                {user.username}
            </Typography>
            <Typography color="textSecondary">
                Created on {user.created}
            </Typography>
        </Paper>
    );

};
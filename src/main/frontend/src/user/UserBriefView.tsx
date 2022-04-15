import { useState, useContext } from 'react';

import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';
import {
        Avatar,
        ListItem, ListItemAvatar, ListItemText,
        Typography
    } from '@material-ui/core';
import HelpOutlineIcon from '@material-ui/icons/HelpOutline';

import { User, UserContext } from './User';
import { UserAvatar } from './UserAvatar';

/*
 * Presents an abbreviated view of a user's information, as a <ListItem> (to be packaged in a <List>).
 * Typically, this would be shown at the head of a post, or in a list of users.
 *
 * Note: you must wrap this inside of a <UserContextProvider> for this to work properly.
 */

const useStyles = makeStyles((theme:Theme) =>
    createStyles({
        root: {
            padding: theme.spacing(2),
            margin: theme.spacing(2),
        }
    }));

type UserBriefViewProps = {
    onClick?: () => void;
};

export const UserBriefView = (props:UserBriefViewProps) => {

    const user = useContext(UserContext);
    const classes = useStyles();

    return(
        <ListItem className={classes.root} button onClick={props.onClick}>
            <ListItemAvatar>
                { user.username ? <UserAvatar user={user} /> : <Avatar><HelpOutlineIcon/></Avatar> }
            </ListItemAvatar>
            <ListItemText primary={ (user.username || "(none)") }/>
        </ListItem>
    );

};
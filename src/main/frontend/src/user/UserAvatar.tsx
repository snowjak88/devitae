import { Avatar } from '@material-ui/core';
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';

import { User } from './User';

const useStyles = makeStyles((theme:Theme) =>
    createStyles({
        avatar: {
            margin: theme.spacing(1),
            backgroundColor: theme.palette.secondary.main,
        },
    }));

type UserAvatarProps = {
  user: User;
};

export const UserAvatar = (props:UserAvatarProps) => {

    const classes = useStyles();
    const initialUppercase = (props.user.username) ? props.user.username?.charAt(0).toUpperCase() + props.user.username?.slice(1) : "?";
    const capitalizeAfterSeparators = initialUppercase.split('_').map(piece => piece.charAt(0).toUpperCase() + piece.slice(1)).join('');
    const onlyFirstInitials = capitalizeAfterSeparators.replace(/[^A-Z]/g, '').substring(0,2);

    return (
        <Avatar alt={props.user.username} className={classes.avatar}>{onlyFirstInitials}</Avatar>
    );
};
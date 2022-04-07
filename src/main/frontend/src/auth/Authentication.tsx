export type Authentication = {
    authenticated: boolean;
    username: string;
    scopes: string[];
    jwt?: string;
};
# StaffAuth Plugin

A simple, lightweight, second-layer authentication plugin designed to add extra security for staff on your Minecraft server.

## How It Works

This plugin does not affect regular players. Only players who are granted the `staffauth.use` permission will be subject to the login & registration system.

When a staff member joins the server, they will be frozen and prompted to log in (After making an account). They cannot move, chat, or interact with the world until they successfully authenticate. This prevents unauthorized access to staff accounts.

## Features

- **Staff-Only Authentication:** Only affects players with the specified permission.
- **Secure Login:** Staff must log in with a separate password after joining.
- **Action Restrictions:** Unauthenticated staff are completely frozen.
- **IP-Based Password Resets:** Staff can only reset their password from a previously used IP address.
- **Secure Storage:** All passwords and IP identifiers are securely hashed.

## Permission

- `staffauth.use` - The one and only permission node. **Grant this to your staff members/groups.**

## Commands

- `/register <password> <confirmPassword>` - Creates your staff account.
- `/login <password>` - Logs into your staff account.
- `/resetpassword <newPassword>` - Resets your password from a recognized IP.

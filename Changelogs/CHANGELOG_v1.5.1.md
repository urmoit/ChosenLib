# ChosenLib v1.5.1 – Changelog

## Bug Fixes

- **NetworkUtils**:
  - Fixed an issue where networking methods were incompatible with the latest Fabric API version. The methods have been updated to use `CustomPayload` for modern, type-safe packet handling. This is a breaking change for any mods that were using the old networking methods.

## General

- Internal version updated to **1.5.1**

---

_This is a patch release that addresses a critical bug in the networking utility, ensuring compatibility with the latest Fabric API. Please review the changes to `NetworkUtils` if you were using it in your project._

# VF-Starter Release Process

Doing a new release consists of the following steps: -

1. Check that all changes from all branches are merged to `main`.
2. Copy latest release notes from `CHANGELOG.md` to `RELEASE-NOTES.md` (**NOTE** - ensure a single line for each item).
3. Run `tag-release.sh` script which parses version from `gradle.properties` and creates and pushes a tag to GIT.
4. Announce the release on various channels.

Pushing a new tag starting with `v` (e.g. `v2022.1`) to GIT will trigger GitHub Action `tag-release.yml`, which will
create a release on GitHub.

After release has been uploaded - update version in `gradle.properties` and add new section in `CHANGELOG.md`.
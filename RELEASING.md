# Release process

TL;DR:

```
./scripts/release.sh
```

## What it does

This should:

1. Determine the new version tag
2. Generate a change log
3. Commit the changelog
4. Give you a command to push (`git push --follow-tags`)

On push to `master`, travis will detect the new tag and perform the sonatype release process.
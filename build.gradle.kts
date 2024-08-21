plugins {
    id("com.github.jmongard.git-semver-plugin") version "0.12.10"
}

semver {
    // Do not create an empty release commit when running the "releaseVersion" task.
    createReleaseCommit = false

    // Do not let untracked files bump the version or add a "-SNAPSHOT" suffix.
    noDirtyCheck = true
}

// Only override a default version (which usually is "unspecified"), but not a custom version.
if (version == Project.DEFAULT_VERSION) {
    version = semver.semVersion.takeIf { it.isPreRelease }
        // To get rid of a build part's "+" prefix because Docker tags do not support it, use only the original "build"
        // part as the "pre-release" part.
        ?.toString()?.replace("${semver.defaultPreRelease}+", "")
            // Fall back to a plain version without pre-release or build parts.
        ?: semver.version
}

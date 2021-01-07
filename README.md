|/  
|\ISS Repo                                                      https://k1ss.org
________________________________________________________________________________


KISS-me - A KISS repository for forks, projects, and fun
________________________________________________________________________________


This branch is meant to stand as a record of the things I had to do to build a
static wyverkiss system. The repository should be fully working. My best
recommendation if you wish to use it is as follows:

1) Don't.

2) Add -static to C(XX)FLAGS

3) Rebuild something. See what breaks. I recommend rebuilding small things first
- `libinput`, for instance. Try to buid `hikari` and see where the build fails.
  If it succeeds, `ldd /usr/bin/hikari` will tell you what shared things you
should work on next.

4) Everything here EXCEPT `mesa` can be built completely statically. Almost no
programs here will link against `libc.so`. 

5) If you want a webbrowser, my recommendation is to use the Qt stack. Odds are,
you won't be able to build a static `qt5-webengine`, but you can most certainly
try. Webkit is also a potential option, though I didn't bother.


### Rationale on `mesa`:

`shared-glapi` will be built if at least two of GL, GLES, and EGL are built.
Because Wayland(?) requires EGL, and EGL requires OpenGL, `mesa` will *always*
build this. Now, you could opt for having OpenGL provided by `libglvnd`, but
`libglvnd` will likewise always provide its own shared-loader. You can peak at
my `mesa` build script for my wonky hack (basically just a bunch of `seds`), but
it will essentially result in being unable to launch a graphical application.
AFAIK, `dlopen()` is used to initialize the graphics drivers, and `musl` uses a
`dlopen()` stub for statically linked objects. This means that it won't work
properly without some *proper* hacking on `mesa`. 

You could maybe technically get away with just using `gbm` from `mesa` with no
`gl` at all, but I'm not sure how far that would go.  Probably  nothing will
work, especially `firefox`. Indeed, building `rust` presented problems. The only
way I could get it to work was by building some shared libs from `llvm`.


### Other sticking points:

`CFLAGS=-static` guarantees you will link against static libraries if available
(usually). It WILL NOT guarantee that you (1) build a static binary, (2) don't
create static libraries.

Many `meson` build systems will default to using `shared_library()` instead of the
*upstream recommended* `library()` so that it can be toggled via
`-Ddefault_library`. `sed` solves this problem, up until a new `meson` version
is dropped that makes adding `version:` entries to static libraries a failure
instead of a warning. It will be soon. 

I have no idea if there's a technically canonical way of making sure `meson`
looks for static libraries; in the meanwhile, appending `static: true` to
relevant `dependency()` lines in the `meson.build` files ensures that `meson`
finds the proper libraries. Otherwise, it might attempt to look for `foo.so` and
complain, or it won't add `-lbar` to the linker flags... and complain.

`cairo` was weird because I had to specify EGL_NO_X11 manually. I feel like it
should be able to identify this itself...

`llvm` is a strange one. If you want to install `libc++abi`, the bundled
`merge-archives.py` script won't actually be able to merge the abi symbols into
`libc++`. However, if you `-DLIBCXXABI_INSTALL_LIBRARY=OFF`, the script works
just fine. Ultimately, there's no good reason to install `libc++abi` anyways;
anything that cares about these symbols will be using `-lc++` and not
`-lc++abi`!  If you need `libc++abi` for some reason, just build it separately.
It takes five seconds.

Ultimately, if things aren't building statically and you want them to, assume
`libtool` is doing some tomfoolery. `LDFLAGS=--static` solves most problems.

If you're using wyverkiss or some other `gcc`-less KISS implementation and want
a working `qt5`, revert `9ad56f3587e572ed87a12a3a9b6641fd9812153c` to get my
5.15.2 build.

--- 


## My other stuff

I am the maintainer of a couple other repositories:

[KISS-haskell](https://github.com/dilyn-corner/KISS-haskell) - a KISS-compliant
repository that contains a bootstrappable `ghc`. 

[KISS-kde](https://github.com/dilyn-corner/KISS-kde) - A KISS-compliant
repository that brings the wonders of a `plasma` desktop to KISS.

[KISS-static](https://github.com/dilyn-corner/KISS_static) - A KISS-compliant
repository that provides a static KISS rootfs.

[dotfiles](https://github.com/dilyn-corner/dotfiles) - Just a simple way to
maintain my dotfiles and share screenshots. Don't use it, just marvel.

---


# Useful fork-facts

## So you made a fork

This is more-so for me and anyone like me who is both new to Git(hub) and forks. 

## Keeping up 

If you want to keep your fork in-line with upstream, there's a very
straightforward way of doing this!<sup>1</sup> Simply add the upstream
repository as a remote URL for git to track, and then you will be able to pull
down any changes upstream makes and merge them into your fork.

```
git remote add upstream $upstreamURL # Add upstream to your remotes
git fetch upstream                   # Fetch upstream's changes 
git checkout master                  # Switch to master
git merge upstream/master            # Merge upstream's master branch
```

Suppose upstream is _very_ busy while you do your work. They push a lot of
changes that you wish you had while you're working on your branch. If you
`merge` upstream into your branch, you'll have some hairy commit messages they
won't _super_ appreciate when you make your PR. So you can rebase instead. It's
very handy.

```
git checkout -b $newBranch
> You do work
> They do work
git fetch upstream
git rebase upstream/master
```

Rebasing<sup>2</sup> is essentially just a patch of the work you did based off
the commit you made your fork from, that you can then simply slide into the
history without making it look wonky (with a bunch of `merge` commit messages).

This will be edited heavily in the future maybe hopefully probably.<sup>3</sup>

<sup>1</sup>[Syncing a fork with upstream](https://www.atlassian.com/git/tutorials/git-forks-and-upstreams)

<sup>2</sup>[Rebasing](https://git-scm.com/book/en/v2/Git-Branching-Rebasing)

<sup>3</sup>[Cleaning commits](https://medium.com/@catalinaturlea/clean-git-history-a-step-by-step-guide-eefc0ad8696d)

---

# Repository trust
All commits signed with my own GPG key.

-----BEGIN PGP PUBLIC KEY BLOCK-----

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
FTjDC7NBgOvvveXb5ccd=j6ND

-----END PGP PUBLIC KEY BLOCK-----

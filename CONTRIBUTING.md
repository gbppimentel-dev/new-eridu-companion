# contributing

Start with a [bug, enhancement or compatibility report](https://github.com/gbppimentel-dev/new-eridu-companion/issues/new/choose).

- **environment:** include app, Android/Thor firmware, Cocoon and ZZZ versions, plus region and whether the optional helper is used.
- **privacy:** use synthetic fixtures. Keep real account responses, UID screenshots, cookies, source databases and signing files out of commits/issues.
- **observations:** unknown is valid. Do not substitute owned agents for undetected squad members or turn visible health bars into an exact enemy census.
- **network:** preserve source cache intervals, scoped authentication recovery and verification/rate-limit backoff.
- **sessions:** preserve task state on Home, explicit new-session identity and user-selected lower-screen apps. Privileged actions must remain fixed-purpose.
- **UI:** review at 538×468; keep ZZZ fonts/art, title-case labels, balanced pills, passive input feedback and quiet explanatory prose.

```sh
node tests/ui-state.cjs
python3 tests/native-checks.py
python3 tests/publication-audit.py
python3 build.py
```

Include behavior, validation and compatibility limits in pull requests. CI is not proof of every agent, account flow or firmware. Original code contributions use MIT; game content and third-party assets keep their separate rights/notices.

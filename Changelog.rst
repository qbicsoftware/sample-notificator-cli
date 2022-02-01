==========
Changelog
==========

This project adheres to `Semantic Versioning <https://semver.org/>`_.

1.3.0 (2022-02-01)
--------------------------

**Added**

* Dynamic email subject containing the project code (#47)

* Improved wording in the email template and link to the sample viewer application (#48)

**Fixed**

* Adapt email sender from ``noreply@qbic.life.com`` to ``noreply@qbic.life``

**Dependencies**

**Deprecated**

1.2.1 (2022-01-19)
------------------

**Added**

**Fixed**

* Adapt email sender from ``noreply.qbic.com`` to ``noreply@qbic.life.com``

**Dependencies**

**Deprecated**

1.2.0 (2022-01-18)
------------------

**Added**

* Provide unsubscription link in email (`#42 <https://github.com/qbicsoftware/sample-notificator-cli/pull/42>`_)

**Fixed**

**Dependencies**

* ``info.picocli.picocli:4.6.1`` -> ``4.6.2``
* ``org.apache.commons.commons-dbcp2:2.7.0`` -> ``2.8.0``
* ``org.jsoup.jsoup:1.13.1`` -> ``1.14.3``
*   Add ``org.springframework.spring-web:5.3.13``

**Deprecated**

1.1.0 (2021-11-23)
------------------

**Added**

* Remove contact email information from HTML template (`#16 <https://github.com/qbicsoftware/sample-notificator-cli/issues/16>`_)

* Update email sender address to a noreply email (`#33 <https://github.com/qbicsoftware/sample-notificator-cli/issues/33>`_)

**Fixed**

**Dependencies**

**Deprecated**

1.0.1 (2021-10-26)
------------------

**Added**

**Fixed**

* ProjectTitle is not mandatory anymore for NotificationContent creation (`#34 <https://github.com/qbicsoftware/sample-notificator-cli/pull/34>`_)

**Dependencies**

**Deprecated**

1.0.0 (2021-10-11)
------------------

**Added**

* Possibility to generate and send HTML based emails informing subscriber about changes in sample status in a project (`#22 <https://github.com/qbicsoftware/sample-notificator-cli/pull/22>`_)

* Introduce logging, Admin notification email template and email sending if the tool failed (`#28 <https://github.com/qbicsoftware/sample-notificator-cli/pull/28>`_)

**Fixed**

**Dependencies**

* Add ``org.jsoup:jsoup:1.13.1``

**Deprecated**


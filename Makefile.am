# Copyright (C) 2011-2012  The OpenTSDB Authors.
#
# This library is free software: you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as published
# by the Free Software Foundation; either version 2.1 of the License, or
# (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this library.  If not, see <http://www.gnu.org/licenses/>.

ACLOCAL_AMFLAGS = -I build-aux

all-am: jar staticroot

package = net.opentsdb
spec_title = OpenTSDB
spec_vendor = StumbleUpon, Inc.
jar := tsdb-$(PACKAGE_VERSION).jar
builddata_SRC := src/BuildData.java
BUILT_SOURCES = $(builddata_SRC)
nodist_bin_SCRIPTS = tsdb
dist_noinst_SCRIPTS = src/create_table.sh
dist_pkgdata_SCRIPTS = src/mygnuplot.sh
tsdb_SRC := \
	src/core/Aggregator.java	\
	src/core/Aggregators.java	\
	src/core/CompactionQueue.java	\
	src/core/Const.java	\
	src/core/DataPoint.java	\
	src/core/DataPoints.java	\
	src/core/DataPointsIterator.java	\
	src/core/IncomingDataPoints.java	\
	src/core/IllegalDataException.java	\
	src/core/Internal.java	\
	src/core/Query.java	\
	src/core/RowKey.java	\
	src/core/RowSeq.java	\
	src/core/SeekableView.java	\
	src/core/Span.java	\
	src/core/SpanGroup.java	\
	src/core/TSDB.java	\
	src/core/Tags.java	\
	src/core/TsdbQuery.java	\
	src/core/WritableDataPoints.java	\
	src/graph/Plot.java	\
	src/stats/Histogram.java	\
	src/stats/StatsCollector.java	\
	src/tools/ArgP.java	\
	src/tools/CliOptions.java	\
	src/tools/CliQuery.java	\
	src/tools/DumpSeries.java	\
	src/tools/Fsck.java	\
	src/tools/TSDMain.java	\
	src/tools/TextImporter.java	\
	src/tools/UidManager.java	\
	src/tsd/BadRequestException.java	\
	src/tsd/ConnectionManager.java	\
	src/tsd/GnuplotException.java	\
	src/tsd/GraphHandler.java	\
	src/tsd/HttpQuery.java	\
	src/tsd/HttpRpc.java	\
	src/tsd/LogsRpc.java	\
	src/tsd/PipelineFactory.java	\
	src/tsd/PutDataPointRpc.java	\
	src/tsd/RpcHandler.java	\
	src/tsd/StaticFileRpc.java	\
	src/tsd/TelnetRpc.java	\
	src/tsd/WordSplitter.java	\
	src/uid/NoSuchUniqueId.java	\
	src/uid/NoSuchUniqueName.java	\
	src/uid/UniqueId.java	\
	src/uid/UniqueIdInterface.java  \
	src/sohuwl/tsd/HttpJsonRpc.java \
	src/sohuwl/tsd/HttpPostRpc.java \
	src/sohuwl/tsd/HttpPutRpc.java  \
	src/sohuwl/utils/Criteria.java  \
	src/sohuwl/utils/JsonBuffer.java    \
	src/sohuwl/utils/JsonEncoder.java

tsdb_DEPS = \
	$(ASYNCHBASE)	\
	$(GUAVA)	\
	$(JCL_OVER_SLF4J)	\
	$(LOG4J_OVER_SLF4J)	\
	$(LOGBACK_CLASSIC)	\
	$(LOGBACK_CORE)	\
	$(NETTY)	\
	$(SLF4J_API)	\
	$(SUASYNC)	\
	$(ZOOKEEPER)    \
	$(JSON)

# Install all the .jar files in pkgdatadir.
pkgdata_DATA = $(tsdb_DEPS) $(jar)

test_SRC := \
	src/core/TestAggregators.java \
	src/core/TestCompactionQueue.java	\
	src/core/TestTags.java	\
	src/stats/TestHistogram.java	\
	src/tsd/TestGraphHandler.java	\
	src/uid/TestNoSuchUniqueId.java	\
	src/uid/TestUniqueId.java

test_DEPS = \
	$(tsdb_DEPS) \
	$(JAVASSIST)	\
	$(JUNIT)	\
	$(MOCKITO)	\
	$(POWERMOCK_MOCKITO)	\
        $(jar)

httpui_SRC := \
	src/tsd/client/DateTimeBox.java	\
	src/tsd/client/EventsHandler.java	\
	src/tsd/client/GotJsonCallback.java	\
	src/tsd/client/MetricForm.java	\
	src/tsd/client/QueryUi.java	\
	src/tsd/client/RemoteOracle.java	\
	src/tsd/client/ValidatedTextBox.java

httpui_DEPS = src/tsd/QueryUi.gwt.xml

dist_pkgdata_DATA = src/logback.xml
dist_static_DATA = src/tsd/static/favicon.ico

EXTRA_DIST = tsdb.in $(tsdb_SRC) $(test_SRC) \
        $(THIRD_PARTY) $(THIRD_PARTY:=.md5) \
        $(httpui_SRC) $(httpui_DEPS) \
        bootstrap build.sh build-aux/gen_build_data.sh $(builddata_SRC)

GWTC_JVM_ARGS =  # add jvmarg -Xss16M or similar if you see a StackOverflowError
GWTC_ARGS = -ea  # Additional arguments like -style PRETTY or -logLevel DEBUG

package_dir := $(subst .,/,$(package))
UNITTESTS := $(test_SRC:src/%.java=$(package_dir)/%.class)
AM_JAVACFLAGS = -Xlint -source 6
JVM_ARGS =
classes := $(tsdb_SRC:src/%.java=$(package_dir)/%.class) \
        $(builddata_SRC:src/%.java=$(package_dir)/%.class)

edit_tsdb_script := srcdir=''; test -f ./$$script.in || srcdir=$(srcdir)/; \
        sed -e "s:@pkgdatadir[@]:$$pkgdatadir:g" \
            -e "s:@abs_srcdir[@]:$$abs_srcdir:g" \
            -e "s:@abs_builddir[@]:$$abs_builddir:g" \
            $${srcdir}$$script.in >$$script.tmp

tsdb: $(srcdir)/tsdb.in
tsdb: Makefile
	rm -f $@ $@.tmp
	script=$@; pkgdatadir=''; \
          abs_srcdir='$(abs_srcdir)'; abs_builddir='$(abs_builddir)'; \
          $(edit_tsdb_script)
	chmod +x $@.tmp
	chmod a-w $@.tmp
	mv $@.tmp $@

printdeps:
	@echo $(tsdb_DEPS) $(jar)

# Adjust the paths in the 'tsdb' script after it's been installed.
# This is kind of a hack, but I couldn't find a better way to adjust the paths
# in the script before it gets installed...
install-exec-hook:
	script=tsdb; pkgdatadir='$(pkgdatadir)'; abs_srcdir=''; abs_builddir=''; \
          $(edit_tsdb_script)
	cat tsdb.tmp >"$(DESTDIR)$(bindir)/tsdb"
	rm -f tsdb.tmp

$(builddata_SRC): .git/HEAD $(tsdb_SRC) $(top_srcdir)/build-aux/gen_build_data.sh
	$(srcdir)/build-aux/gen_build_data.sh $(builddata_SRC) $(package)

jar: $(jar) .javac-unittests-stamp .gwtc-stamp

JAVA_COMPILE := $(JAVAC) $(AM_JAVACFLAGS) -d .

# Sets the shell variable $src to the list of `.java' files we depend on
# that need to be recompiled.  Runs "exit 0" if there's nothing to compile,
# which can happen if only non-.java dependencies have been updated.
filter_src = \
 src=''; \
 for i in $?; do \
   case $$i in (*.java) \
     test -f "$(srcdir)/$$i" && i="$(srcdir)/$$i"; \
     src="$$src $$i";; \
   esac; \
 done; \
 test -n "$$src" || exit 0
# Touches all the targets if any of the dependencies are newer.
# This is useful to force-recompile all files if one of the
# dependent libraries gets updated.
refresh_src = \
 for i in $@; do \
   test -f "$$i" && touch "$$i" && continue; \
   i="$(srcdir)/$$i"; \
   test -f "$$i" && touch "$$i" && continue; \
 done; :

$(tsdb_SRC): $(tsdb_DEPS)
	@$(refresh_src)

find_jar = test -f "$$jar" && echo "$$jar" || echo "$(srcdir)/$$jar"
get_dep_classpath = `for jar in $(tsdb_DEPS); do $(find_jar); done | tr '\n' ':'`
.javac-stamp: $(tsdb_SRC) $(builddata_SRC)
	@$(filter_src); cp=$(get_dep_classpath); \
          echo "$(JAVA_COMPILE) -cp $$cp $$src"; \
                $(JAVA_COMPILE) -cp $$cp $$src
	@touch "$@"

GWT_CLASSPATH = `jar=$(GWT_DEV); $(find_jar)`:`jar=$(GWT_USER); $(find_jar)`:$(srcdir)/src
# The GWT compiler is way too slow, that's not very Googley.  So we save the
# MD5 of the files we compile in the stamp file and everytime `make' things it
# needs to recompile the GWT code, we verify whether the code really changed
# or whether it's just a file that was touched (which happens frequently when
# using Git while rebasing and whatnot).
gwtc: .gwtc-stamp
.gwtc-stamp: $(httpui_SRC) $(httpui_DEPS) $(GWT_DEV) $(GWT_USER)
	@$(mkdir_p) gwt
	{ cd $(srcdir) && cat $(httpui_SRC); } | $(MD5) >"$@-t"
	cmp -s "$@" "$@-t" && exit 0; \
        $(JAVA) $(GWTC_JVM_ARGS) -cp $(GWT_CLASSPATH) com.google.gwt.dev.Compiler \
            $(GWTC_ARGS) -war gwt tsd.QueryUi
	@mv "$@-t" "$@"

DEV_TSD_ARGS = \
  --port=$(DEV_TSD_PORT) \
  --staticroot=$(DEV_TSD_STATICROOT) --cachedir=$(DEV_TSD_CACHEDIR)
DEV_TSD_PORT = 4242
DEV_TSD_STATICROOT = staticroot
DEV_TSD_CACHEDIR = /tmp/tsd
GWT_DEV_URL = http://127.0.0.1:$(DEV_TSD_PORT)/

GWT_DEV_ARGS = -Xmx512m  # The development mode is a memory hog.
gwtdev: .gwtc-stamp
	$(JAVA) $(GWT_DEV_ARGS) -ea -cp $(GWT_CLASSPATH) com.google.gwt.dev.DevMode \
	  -startupUrl $(GWT_DEV_URL) -noserver -war gwt tsd.QueryUi

staticroot: jar .staticroot-stamp

gwttsd: staticroot
	./tsdb tsd $(DEV_TSD_ARGS)

.staticroot-stamp: $(dist_static_DATA) .gwtc-stamp
	$(mkdir_p) $(DEV_TSD_STATICROOT)
	cp $(dist_static_DATA:%=$(srcdir)/%) $(DEV_TSD_STATICROOT)
	find -L $(DEV_TSD_STATICROOT) -type l -exec rm {} \;
	p=`pwd`/gwt/queryui && cd $(DEV_TSD_STATICROOT) \
	  && for i in $$p/*; do ln -s -f "$$i" || break; done
	find -L $(DEV_TSD_STATICROOT)/gwt -type f | xargs touch
	@touch .staticroot-stamp

# Install all the files from the static dir into $staticdir.
# Ideally I'd like Automake to take care of this, but right now I don't see
# how to tell it to install a bunch of files recursively for which I don't
# know ahead of time what the file names are.
install-data-local: staticroot
	@$(NORMAL_INSTALL)
	test -z "$(staticdir)" || $(mkdir_p) "$(DESTDIR)$(staticdir)"
	@set -e; pwd; ls -lFh; cd "$(DEV_TSD_STATICROOT)"; \
          list=`find -L . ! -type d`; for p in $$list; do \
          p=$${p#./}; \
	  if test -f "$$p"; then d=; else d="$(srcdir)/"; fi; \
          dstdir=`dirname "$(DESTDIR)$(staticdir)/$$p"`; \
          if test -d "$$dstdir"; then :; else \
            echo " $(mkdir_p) '$$dstdir'"; $(mkdir_p) "$$dstdir"; fi; \
	  echo " $(INSTALL_DATA) '$$d$$p' '$(DESTDIR)$(staticdir)/$$p'"; \
	  $(INSTALL_DATA) "$$d$$p" "$(DESTDIR)$(staticdir)/$$p"; \
	done

uninstall-local:
	@$(NORMAL_UNINSTALL)
	rm -rf "$(DESTDIR)$(staticdir)"

# I don't know why Automake leaves this directory behind.
uninstall-hook:
	test ! -d "$(pkgdatadir)" || rmdir "$(pkgdatadir)"

get_runtime_dep_classpath = `for jar in $(test_DEPS); do $(find_jar); done | tr '\n' ':'`
$(test_SRC): $(test_DEPS)
	@$(refresh_src)
.javac-unittests-stamp: $(jar) $(test_SRC)
	@$(filter_src); cp=$(get_runtime_dep_classpath); \
          echo "$(JAVA_COMPILE) -cp $$cp $$src"; \
                $(JAVA_COMPILE) -cp $$cp $$src
	@touch "$@"
	@touch .javac-unittests-stamp

classes_with_nested_classes := $(classes:.class=*.class)
test_classes_with_nested_classes := $(UNITTESTS:.class=*.class)

# Little set script to make a pretty-ish banner.
BANNER := sed 's/^.*/  &  /;h;s/./=/g;p;x;p;x'
check-local: .javac-unittests-stamp
	classes=`echo $(test_classes_with_nested_classes)` \
        && tests=0 && failures=0 \
        && cp="$(get_runtime_dep_classpath):$(srcdir)/src" && \
        for i in $$classes; do \
          case $$i in (*[$$]*) continue;; esac; \
          tests=$$((tests + 1)); \
          echo "Running tests for `basename $$i .class`" | $(BANNER); \
          $(JAVA) -ea -esa $(JVM_ARGS) -cp "$$cp" org.junit.runner.JUnitCore `echo $${i%.class} | tr / .` $(ARGS) \
          || failures=$$((failures + 1)); \
        done; \
        if test "$$failures" -eq 0; then \
          echo "All $$tests tests passed" | $(BANNER); \
        else \
          echo "$$failures out of $$tests failed, please send a report to $(PACKAGE_BUGREPORT)" | $(BANNER); \
        fi

git_version := \
  `git rev-list --pretty=format:%h HEAD --max-count=1 | sed 1d || echo unknown`
manifest: .javac-stamp .git/HEAD
	{ echo "Specification-Title: $(spec_title)"; \
          echo "Specification-Version: $(PACKAGE_VERSION)"; \
          echo "Specification-Vendor: $(spec_vendor)"; \
          echo "Implementation-Title: $(package)"; \
          echo "Implementation-Version: $(git_version)"; \
          echo "Implementation-Vendor: $(spec_vendor)"; } >"$@"

$(jar): manifest .javac-stamp $(classes)
	$(JAR) cfm `basename $(jar)` manifest $(classes_with_nested_classes) \
         || { rv=$$? && rm -f `basename $(jar)` && exit $$rv; }
#                       ^^^^^^^^^^^^^^^^^^^^^^^
# I've seen cases where `jar' exits with an error but leaves a partially built .jar file!

# Generate the file for those who get a tarball without it.  This happens if
# you download a tarball off GitHub for instance.
.git/HEAD:
	echo unknown >$@

JAVADOC_DIR = api
doc: $(JAVADOC_DIR)/index.html

JDK_JAVADOC = http://download.oracle.com/javase/6/docs/api
NETTY_JAVADOC = http://docs.jboss.org/netty/$(NETTY_MAJOR_VERSION)/api
SUASYNC_JAVADOC = http://tsunanet.net/~tsuna/async/$(SUASYNC_VERSION)
$(JAVADOC_DIR)/index.html: $(tsdb_SRC)
	$(JAVADOC) -d $(JAVADOC_DIR) -classpath $(get_dep_classpath) \
          -link $(JDK_JAVADOC) -link $(NETTY_JAVADOC) -link $(SUASYNC_JAVADOC) \
          $? $(builddata_SRC)

dist-hook:
	$(mkdir_p) $(distdir)/.git
	echo $(git_version) >$(distdir)/.git/HEAD

mostlyclean-local:
	@rm -f .javac-stamp .javac-unittests-stamp .gwtc-stamp* .staticroot-stamp
	rm -rf gwt gwt-unitCache staticroot
	rm -f manifest $(BUILT_SOURCES)
	rm -f $(classes_with_nested_classes) $(test_classes_with_nested_classes)
	test -d $(package_dir) || exit 0 \
	  && find $(package_dir) -depth -type d -exec rmdir {} ';' \
	  && dir=$(package_dir) && dir=$${dir%/*} \
	  && while test x"$$dir" != x"$${dir%/*}"; do \
	       rmdir "$$dir" && dir=$${dir%/*} || break; \
	     done \
	  && rmdir "$$dir"

clean-local:
	rm -f $(jar) tsdb
	rm -rf $(JAVADOC_DIR)

.PHONY: jar doc check gwtc gwtdev printdeps staticroot gwttsd
include third_party/include.mk

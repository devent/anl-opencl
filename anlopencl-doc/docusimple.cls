% This file is part of Latexlibs.
% Copyright 2013 Erwin Müller, erwin.mueller@deventm.org.
% This work is licensed under the Creative Commons Attribution 3.0 Unported
% License. To view a copy of this license, visit
% http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
% Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
\ProvidesClass{docusimple}[2013/05/27 Class for short documents or articles.]

%%%
% Usefull package options for scrreprt:
%
% abstract  = on|off
% toc       = flat,graduated,index,nulistof,nobib,noindex,nolistof
%%%

% extends scrreprt class
% Read all the documentclass options; pass them to scrreprt, unless the file
% named "<currentoption>.min" exists, in which case it is loaded
\DeclareOption*{\InputIfFileExists{\CurrentOption.min}{}{%
    \PassOptionsToClass{\CurrentOption}{scrartcl}}}
\ProcessOptions \relax
\LoadClass{scrartcl}

%
\RequirePackage[utf8x]{inputenc}
%
\RequirePackage[hscale=0.69,vscale=0.79,heightrounded,includehead]{geometry}
% Font
\RequirePackage{bookman}
\RequirePackage[T1]{fontenc}
% header and footer
\RequirePackage{scrheadings}
% include graphics
\RequirePackage{graphicx}
% for the table commands
%   * toprule
%   * midrule
%   * bottomrule
\RequirePackage{booktabs}
% for the new list environments
%   * asparaenum
%   * inparaenum
%   * compactenum
%   * asparaitem
%   * inparaitem
%   * compactitem
%   * asparadesc
%   * inparadesc
%   * compactdesc
\RequirePackage{paralist}
% multiple figures
\RequirePackage{subcaption}
% math fonts and symbols
\RequirePackage{amssymb,amsmath}
\RequirePackage[nodisplayskipstretch]{setspace}
\setstretch{1.0}
% coloured hyperlinks
\RequirePackage{colorhyper}
% custom styles
\RequirePackage{floatlengths}
% custom styles
\RequirePackage{customstyles}

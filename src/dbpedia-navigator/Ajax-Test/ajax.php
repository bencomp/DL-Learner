<?php
require_once ("xajax/xajax_core/xajax.inc.php");
$sid = session_id();

$xajax = new xajax("ajaxfunctions.php?sid=$sid");
$xajax->register(XAJAX_FUNCTION, 'getsubjects', array(
    'onResponseDelay' => 'showLoadingSubjects',
    'beforeResponseProcessing' => 'hideLoadingSubjects'
    ));
$xajax->register(XAJAX_FUNCTION, 'getarticle', array(
    'onResponseDelay' => 'showLoadingArticle',
    'beforeResponseProcessing' => 'hideLoadingArticle'
    ));
$xajax->registerFunction('addPositive');
$xajax->registerFunction('addNegative');
$xajax->registerFunction('clearPositives');
$xajax->registerFunction('clearNegatives');
$xajax->register(XAJAX_FUNCTION, 'learnConcept', array(
    'onResponseDelay' => 'showLoadingConcept',
    'beforeResponseProcessing' => 'hideLoadingConcept'
    ));
$xajax->register(XAJAX_FUNCTION, 'getSubjectsFromConcept', array(
    'onResponseDelay' => 'showLoadingConceptSubjects',
    'beforeResponseProcessing' => 'hideLoadingConceptSubjects'
    ));
$xajax->registerFunction('searchAndShowArticle');
$xajax->register(XAJAX_FUNCTION, 'showThisSearchResult', array(
	'onResponseDelay' => 'showLoadingSubjects',
    'beforeResponseProcessing' => 'hideLoadingSubjects'
	));
$xajax->registerFunction('showThisArticle');
?>
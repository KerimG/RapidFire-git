**FREE

//*-----------------------------------------------------------------------------------------*
//*                                                                                         *
//* Copyright (c) 2016 Task Force IT-Consulting GmbH, Waltrop (Germany)                     *
//* This software and the accompanying materials are made available                         *
//* under the terms of the GNU General Public License version 2.0 (GPLv2)                   *
//* which accompanies this distribution and is available at                                 *
//* http://www.gnu.org/licenses/gpl-2.0.html                                                *
//*                                                                                         *
//*-----------------------------------------------------------------------------------------*
//*                                                                                         *
//* Rapid Fire                                                                              *
//*                                                                                         *
//*------------+--------+-------------------------------------------------------------------*
//*   Datum    I Progr. I Beschreibung                                                      *
//*------------+--------+-------------------------------------------------------------------*
//* 01.07.2014 I   FH   I Programm erstellt                                                 *
//*------------+--------+-------------------------------------------------------------------*
//* >>PRE-COMPILER<<                                                                        *
//*   >>CRTCMD<< CRTBNDRPG PGM(&LI/&OB) SRCFILE(&SL/&SF) SRCMBR(&SM);                       *
//*   >>IMPORTANT<<                                                                         *
//*     >>PARM<< DFTACTGRP(*NO);                                                            *
//*     >>PARM<< DBGVIEW(&U1);                                                              *
//*     >>PARM<< ACTGRP(&U2);                                                               *
//*     >>PARM<< TGTRLS(&TR);                                                               *
//*     >>PARM<< OPTION(*EVENTF);                                                           *
//*   >>END-IMPORTANT<<                                                                     *
//*   >>EXECUTE<<                                                                           *
//* >>END-PRE-COMPILER<<                                                                    *
//*-----------------------------------------------------------------------------------------*

// Control

CTL-OPT DatFmt(*ISO) TimFmt(*ISO) DecEdit('0,');
CTL-OPT DftActGrp(*NO) ActGrp('RAPIDFIRE') BNDDIR('RAPIDFIRE');
CTL-OPT Copyright('(C) Copyright Task Force IT-Consulting GmbH');
CTL-OPT ExtBinInt(*YES);

//*-----------------------------------------------------------------------------------------*

DCL-F RFINTRO Workstn(*EXT) USAGE(*INPUT:*OUTPUT);

//*-----------------------------------------------------------------------------------------*

// Prozedurenprototyp f�r Prozedur 'RF'

DCL-PR RF;
END-PR;

// Prozedurenschnittstelle f�r Prozedur 'RF'

DCL-PI RF;
END-PI;

//*-----------------------------------------------------------------------------------------*

// Prozedurenprototyp f�r Prozedur 'RF0001PG'

/Copy QCPYSRC,RF0001PG

// Prozedurenprototypen f�r Modul 'EXCCMD'

/Copy QCPYSRC,EXCCMD

// Prozedurenprototyp f�r Prozedur 'QTNRCMTI'

/Copy QCPYSRC,QTNRCMTI

// Datenstruktur f�r Format 'CMTI0100' f�r Prozedur 'QTNRCMTI'

/Copy QCPYSRC,CMTI0100

// Datenstruktur f�r Format 'ERRC0100' f�r Fehlercode

/Copy QCPYSRC,ERRC0100

// Sonstige Felddeklarationen

DCL-S ITSts CHAR(10);                                                    // Nachrichten-Id. b
DCL-S ITPrc CHAR(10);                                                    // Verarbeitung
DCL-S ITJOB CHAR(10);                                                    // Job
DCL-S ITRtCd CHAR(10);                                                   // R�ckkehrcode

//*-----------------------------------------------------------------------------------------*

// Commitment Informationen auffinden
Reset ERRC0100;
QTNRCMTI(
 CMTI0100 :                                                              // <-- Receiver vari
 %Size(CMTI0100) :                                                       // --> Length of rec
 'CMTI0100' :                                                            // --> Format name
 ERRC0100                                                                // <-> Error code
);
// Commitment Informationen wurden aufgefunden
If ERRC0100.Exeption_Id = *Blanks;
  // Verarbeitung initialisieren
  ITPrc = '*YES';
  // Commitment Control muss gestartet werden
  If CMTI0100.Commit_Definition_Status = 'I';
    // Commitment-Control starten
    EXCCMD_execute_Command(
     'STRCMTCTL ' +
     'LCKLVL(*CHG) ' +
     'CMTSCOPE(*ACTGRP) ' +
     'TEXT(''Rapid Fire'')'
    );
    // Commitment-Control konnte nicht gestartet werden
    If EXCCMD_Message_Id <> *Blanks;
      ITPrc = '*NO';
    EndIf;
  EndIf;
  If ITPrc = '*YES';
    // Show main panel
    ExFmt DSPF010;
    // Maintain Jobs
    RF0001PG(
     '*EDIT' :                                                           // --> Modus
     *Blanks :                                                           // --> Filter Job
     ITJOB :                                                             // <-> Job
     ITRtCd                                                              // <-- R�ckkehrcode
    );
    // Commitment Control muss beendet werden
    If CMTI0100.Commit_Definition_Status = 'I';
      // Commitment-Control beenden
      EXCCMD_execute_Command(
      'ENDCMTCTL'
      );
    EndIf;
  EndIf;
EndIf;
// Programm nicht hauptspeicherresident verlassen
*INLR = *On;
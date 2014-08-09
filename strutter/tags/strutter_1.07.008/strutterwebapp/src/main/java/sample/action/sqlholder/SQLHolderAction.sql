main:

-- $localisationkey

SELECT --
	tab02.ext_debitoren_nr "$proud.table.ext_debitoren_nr", 
	tab02.debitoren_nr "$proud.table.debitoren_nr",
	tab02.de_name_plz_ort "$proud.table.de_name_plz_ort", 
	tab03.belegart || ' ' || b.text "$proud.table.belegart",
	tab03.belegnummer "$proud.table.belegnummer", 
	format_date(tab03.belegdatum) "$proud.table.belegdatum",
	cast(format_amount(tab03.kz_soll_haben, tab03.rechnungsendwert) as number(10,2)) "$proud.table.rechnungsendwert",  
	'$proud.table.eur'  "$proud.table.wkz",
	cast ( format_amount('1', tab02.KREDITLIMIT+tab02.FINANZLIMIT) as number(10,2) ) "$proud.table.limit" 
FROM 
	proud_fias1_6680_02 tab02, 
	proud_fias1_6680_03 tab03, 
	proud_import_daily_status i, 
	proud_belegart b 
WHERE 
	tab03.belegart = b.belegnr (+) AND 
	tab03.belegart in (551, 414, 554, 556) AND 
	tab02.import_daily_status_id = i.id AND 
	tab02.id = tab03.fias1_6680_02_id (+) AND 
	tab03.kz_soll_haben = 2 

	:damdam

			if(faellBis !=null && faellBis.trim().length() == 10){
				String formatFaellBis = faellBis.trim().substring(6)+faellBis.trim().substring(3,5)+faellBis.trim().substring(0,2);
				selectStatement= selectStatement +  "  AND  tab03.FAELLIGKEITSDATUM <= "+formatFaellBis ;
			}
			if(faellVon !=null && faellVon.trim().length() == 10){
				String formatFaellVon = faellVon.trim().substring(6)+faellVon.trim().substring(3,5)+faellVon.trim().substring(0,2);
				selectStatement= selectStatement +  "  AND  tab03.FAELLIGKEITSDATUM >= "+ formatFaellVon ;
			}
			if(belegBis !=null && belegBis.trim().length() == 10){
				String formatBelegBis = belegBis.trim().substring(6)+belegBis.trim().substring(3,5)+belegBis.trim().substring(0,2);
				selectStatement= selectStatement +  "  AND  tab03.BELEGDATUM <= "+formatBelegBis ;
			}
			if(belegVon !=null && belegVon.trim().length() == 10){
				String formatBelegVon = belegVon.trim().substring(6)+belegVon.trim().substring(3,5)+belegVon.trim().substring(0,2);
				selectStatement= selectStatement +  "  AND  tab03.BELEGDATUM >= "+ formatBelegVon ;
			}

//			if(datumBis !=null && datumBis.trim().length() == 10){
//				String formatDatumBis = datumBis.trim().substring(8)+datumBis.trim().substring(3,5)+datumBis.trim().substring(0,2);
//				selectStatement= selectStatement +  "  and  i.filedate <= "+ formatDatumBis ;
//			}
			if(datumVon !=null && datumVon.trim().length() == 10){
				String formatDatumVon = datumVon.trim().substring(8)+datumVon.trim().substring(3,5)+datumVon.trim().substring(0,2);
				selectStatement= selectStatement +  "  AND  i.filedate = "+ formatDatumVon ;
			}
			if(belegNr!=null && belegNr.trim().length()> 0){
				selectStatement= selectStatement +  "  AND  trim(tab03.BELEGNUMMER)='"+ belegNr+"' ";
			}
			if(debNrExt != null && debNrExt.trim().length()> 0){
				selectStatement= selectStatement + "  AND  trim(tab02.EXT_DEBITOREN_NR)='"+ debNrExt + "'";
			}
			if(debNrInt != null && debNrInt.trim().length()> 0){
				selectStatement= selectStatement + "  AND  trim(tab02.DEBITOREN_NR)='"+ debNrInt + "'";
			}

			selectStatement+= "  AND  tab02.as_kunden_nr='"+ askNr + "' " +
							  "  AND  tab02.mandanten_id='"+ mandantenId + "' ";

			selectStatement += ProudUtil.sqlStatementForFilter( askNr, ProudMRGutschriftenAction.class.getName() );

			selectStatement += " ORDER BY i.filedate , tab02.DEBITOREN_NR, tab03.belegnummer";

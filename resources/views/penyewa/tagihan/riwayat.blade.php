@inject('helper', 'App\Helpers\HelpersFunction')
@extends('penyewa.layout')
@section('content')
<!-- Content Header (Page header) -->
<section class="content-header">
   <div class="container-fluid">
      <div class="row mb-2">
         <div class="col-sm-6">
            <h1>Data Riwayat Pembayaran</h1>
         </div>
         <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
               <li class="breadcrumb-item"><a href="{{ URL::to('/penyewa') }}">Home</a></li>               
               <li class="breadcrumb-item active">Riwayat Pembayaran</li>
            </ol>
         </div>
      </div>
   </div>
   <!-- /.container-fluid -->
</section>
<section class="content">
   <div class="container-fluid">
      <div class="row">
         <div class="col-12">
            <div class="card">
               <div class="card-header container-fluid">
                  <div class="row">
                     <div class="col-md-10">
                        <h3 class="card-title">Data Riwayat Pembayaran</h3>
                     </div>
                     <div class="col-md-2">
                        
                     </div>
                  </div>
               </div>
               <!-- /.card-header -->
               <div class="card-body">
                  <table id="dataTables" class="table table-bordered table-striped">
                     <thead>
                        <tr>
                           <th>Nama Rumah Kos</th> 
                           <th>Nama Kamar</th>                         
                           <th>Tgl. Jatuh Tempo</th>
                           <th>Tgl. Bayar</th>
                           <th>Bulan Sewa</th>
                           <th>Harga Total</th>  
                        </tr>
                     </thead>
                     <tbody>
                        @foreach($data as $d)
                        <tr>
                           <td>{{ $d->nama_kos }}</td>     
                           <td>{{ $d->nama_kamar }}</td>                         
                           <td>{!! $d->tgl_jatuh_tempo !!}</td>
                           <td>{!! $d->tgl_bayar !!}</td>
                           <td>{!! $d->bulan_sewa  . ' Bulan' !!}</td>
                           <td>{!! $helper->rupiah($d->harga) !!}</td>  
                        </tr>
                        @endforeach
                     </tbody>
                  </table>
               </div>
               <!-- /.card-body -->
            </div>
            <!-- /.card -->
         </div>
      </div>
   </div>
</section>
<script>
   $(function () {
     
     $('#dataTables').DataTable({
       "paging": true,
       "lengthChange": false,
       "searching": true,
       "ordering": true,
       "info": true,
       "autoWidth": false,
       "responsive": true,
     });
   });
</script>
@endsection